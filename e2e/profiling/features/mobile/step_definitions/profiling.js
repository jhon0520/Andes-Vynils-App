'use strict';
// Profiling steps. Kraken drives the UI with the steps in e2e/kraken, and these steps read with adb
// what the app consumes meanwhile: CPU and threads from /proc/<pid>, memory from dumpsys meminfo,
// frames from dumpsys gfxinfo, API requests from the OkHttp log in logcat, ANR and crashes.
// Each scenario writes results/<build>/<device>/<scenario>-run<N>.json; summarize.js builds the tables.
const assert = require('assert');
const fs = require('fs');
const path = require('path');
const { execFile, execFileSync } = require('child_process');
const { promisify } = require('util');

// Same cucumber instance as Kraken: it lives in e2e/kraken/node_modules.
const KRAKEN_DIR = path.resolve(__dirname, '../../../../kraken');
const { Given, When, Then } = require(require.resolve('@cucumber/cucumber', { paths: [KRAKEN_DIR] }));

const PKG = 'com.dev.wine.music.app';
const ACTIVITY = `${PKG}/.MainActivity`;
const TIMEOUT = 20000;
// Short poll so the measured time is close to when the screen really shows up.
const POLL_MS = 50;
const SAMPLE_MS = 500;
const RESULTS_DIR = path.resolve(__dirname, '../../../results');
const BUILD = process.env.PROFILE_BUILD || 'sin-etiqueta';
const RUN = Number(process.env.PROFILE_RUN || 1);

const byId = (id) => `android=new UiSelector().resourceId("${id}")`;
const byIdPrefix = (prefix) => `android=new UiSelector().resourceIdMatches("${prefix}.*")`;
const byTextContaining = (text) => `android=new UiSelector().textContains("${text}")`;

const execFileAsync = promisify(execFile);
const sleep = (ms) => new Promise((resolve) => setTimeout(resolve, ms));
const number = (text, re) => {
  const m = text.match(re);
  return m ? Number(m[1]) : null;
};

function shell(serial, cmd) {
  return execFileSync('adb', ['-s', serial, 'shell', cmd], { encoding: 'utf8', maxBuffer: 64 * 1024 * 1024 });
}

function tryShell(serial, cmd) {
  try {
    return shell(serial, cmd);
  } catch (e) {
    return '';
  }
}

async function shellAsync(serial, cmd) {
  const { stdout } = await execFileAsync('adb', ['-s', serial, 'shell', cmd], { encoding: 'utf8' });
  return stdout;
}

// ---------- device ----------

const factsBySerial = new Map();

function deviceFacts(serial) {
  if (factsBySerial.has(serial)) return factsBySerial.get(serial);
  const props = {};
  for (const line of shell(serial, 'getprop').split('\n')) {
    const m = line.match(/^\[([^\]]+)\]: \[(.*)\]$/);
    if (m) props[m[1]] = m[2];
  }
  const maxFreqs = tryShell(serial, 'cat /sys/devices/system/cpu/cpu*/cpufreq/cpuinfo_max_freq')
    .split(/\s+/).filter(Boolean).map(Number);
  const memTotalKb = number(tryShell(serial, 'cat /proc/meminfo'), /MemTotal:\s+(\d+)/) || 0;
  const facts = {
    manufacturer: props['ro.product.manufacturer'] || '',
    model: props['ro.product.model'] || 'desconocido',
    marketName: props['ro.product.marketname'] || props['ro.config.marketing_name'] || '',
    androidRelease: props['ro.build.version.release'] || '',
    sdk: Number(props['ro.build.version.sdk'] || 0),
    soc: [props['ro.soc.manufacturer'], props['ro.soc.model']].filter(Boolean).join(' ')
      || props['ro.board.platform'] || props['ro.hardware'] || '',
    cores: Number(tryShell(serial, 'nproc').trim()) || maxFreqs.length || 1,
    cpuMaxGhz: maxFreqs.length ? Math.round(Math.max(...maxFreqs) / 1e4) / 100 : null,
    ramGb: Math.round((memTotalKb / 1024 / 1024) * 10) / 10,
    screen: (tryShell(serial, 'wm size').match(/(\d+x\d+)/) || [])[1] || '',
    density: number(tryShell(serial, 'wm density'), /(\d+)/),
    clkTck: Number(tryShell(serial, 'getconf CLK_TCK').trim()) || 100,
  };
  factsBySerial.set(serial, facts);
  return facts;
}

function deviceDir(facts) {
  return path.join(RESULTS_DIR, BUILD, facts.model.replace(/[^A-Za-z0-9._-]+/g, '-'));
}

// ---------- process ----------

async function waitForPid(serial) {
  for (let i = 0; i < 40; i++) {
    const pid = Number(tryShell(serial, `pidof ${PKG}`).trim().split(/\s+/)[0]);
    if (pid) return pid;
    await sleep(250);
  }
  throw new Error(`${PKG} is not running`);
}

// utime + stime (fields 14 and 15) of a /proc/<pid>/stat line, in clock ticks.
// Field 2 is the name in parentheses and may hold spaces, so fields are counted after the last ')'.
function cpuTicks(statLine) {
  const fields = statLine.slice(statLine.lastIndexOf(')') + 2).trim().split(/\s+/);
  return Number(fields[11]) + Number(fields[12]);
}

// The main thread has tid == pid, so /proc/<pid>/task/<pid>/stat is the time spent on `main`.
const statCommand = (pid) => `cat /proc/${pid}/stat /proc/${pid}/task/${pid}/stat /proc/${pid}/status`;

function parseStat(out) {
  const lines = out.split('\n');
  return {
    total: cpuTicks(lines[0]),
    main: cpuTicks(lines[1]),
    rssKb: number(out, /VmRSS:\s+(\d+)/),
    threads: number(out, /Threads:\s+(\d+)/),
  };
}

// Reads CPU, RSS and threads every SAMPLE_MS in the background to get the peaks.
function startSampler(profile) {
  const sampler = { running: true, samples: [] };
  sampler.done = (async () => {
    let previous = null;
    while (sampler.running) {
      try {
        const t = Date.now();
        const sample = { t, ...parseStat(await shellAsync(profile.serial, statCommand(profile.pid))) };
        if (previous) {
          const seconds = (t - previous.t) / 1000;
          sample.cpuPct = ((sample.total - previous.total) / profile.facts.clkTck / seconds / profile.facts.cores) * 100;
        }
        sampler.samples.push(sample);
        previous = sample;
      } catch (e) {
        // A missed sample only lowers the resolution of the peaks.
      }
      await sleep(SAMPLE_MS);
    }
  })();
  return sampler;
}

// ---------- logcat ----------

const REQUEST = /-->\s+(GET|POST|PUT|PATCH|DELETE)\s+(\S+)/;
const RESPONSE = /<--\s+(\d{3})\b.*?\s(https?:\/\/\S+)\s+\((\d+)ms/;
const urlPath = (url) => url.replace(/^https?:\/\/[^/]+/, '');

// The OkHttp logging interceptor (debug builds) writes "--> GET <url>" for each API call.
// Covers are loaded by Coil with its own client, so they are not counted here.
function readNetwork(profile) {
  const out = tryShell(profile.serial, `logcat -d -v brief --pid=${profile.pid}`);
  const requests = [];
  const responses = [];
  for (const line of out.split('\n')) {
    const req = line.match(REQUEST);
    if (req) requests.push({ method: req[1], path: urlPath(req[2]) });
    const res = line.match(RESPONSE);
    if (res) responses.push({ status: Number(res[1]), path: urlPath(res[2]), ms: Number(res[3]) });
  }
  return { requests, responses };
}

// ---------- dumpsys ----------

function parseMeminfo(out) {
  return {
    totalPssKb: number(out, /TOTAL PSS:\s+(\d+)/) ?? number(out, /TOTAL:\s+(\d+)/),
    javaHeapKb: number(out, /Java Heap:\s+(\d+)/),
    nativeHeapKb: number(out, /Native Heap:\s+(\d+)/),
    graphicsKb: number(out, /Graphics:\s+(\d+)/),
    codeKb: number(out, /Code:\s+(\d+)/),
    activities: number(out, /Activities:\s+(\d+)/),
    appContexts: number(out, /AppContexts:\s+(\d+)/),
  };
}

function parseGfxinfo(out) {
  const histogram = (out.match(/^\s*HISTOGRAM:\s*(.*)$/m) || [])[1] || '';
  let slow = 0;
  let frozen = 0;
  for (const bucket of histogram.trim().split(/\s+/)) {
    const m = bucket.match(/^(\d+)ms=(\d+)$/);
    if (!m) continue;
    if (Number(m[1]) > 16) slow += Number(m[2]);
    if (Number(m[1]) >= 700) frozen += Number(m[2]);
  }
  const janky = out.match(/Janky frames:\s+(\d+)\s+\(([\d.]+)%\)/);
  return {
    total: number(out, /Total frames rendered:\s+(\d+)/),
    janky: janky ? Number(janky[1]) : null,
    jankyPct: janky ? Number(janky[2]) : null,
    slowOver16ms: slow,
    frozenOver700ms: frozen,
    p50Ms: number(out, /50th percentile:\s+(\d+)ms/),
    p90Ms: number(out, /90th percentile:\s+(\d+)ms/),
    p95Ms: number(out, /95th percentile:\s+(\d+)ms/),
    p99Ms: number(out, /99th percentile:\s+(\d+)ms/),
    slowUiThread: number(out, /Number Slow UI thread:\s+(\d+)/),
    deadlineMissed: number(out, /Number Frame deadline missed:\s+(\d+)/),
  };
}

// ---------- steps ----------

Given('I record the device information', function () {
  const facts = deviceFacts(this.device.id);
  const dir = deviceDir(facts);
  fs.mkdirSync(dir, { recursive: true });
  const { clkTck, ...shown } = facts;
  fs.writeFileSync(path.join(dir, 'device.json'), `${JSON.stringify(shown, null, 2)}\n`);
});

// `am start -S -W` kills the app and opens it again: TotalTime is the cold start until the first frame.
When('I measure the cold start {int} times', async function (times) {
  const runs = [];
  for (let i = 0; i < times; i++) {
    const out = shell(this.device.id, `am start -S -W -n ${ACTIVITY}`);
    runs.push({
      totalTimeMs: number(out, /TotalTime:\s+(\d+)/),
      waitTimeMs: number(out, /WaitTime:\s+(\d+)/),
      launchState: (out.match(/LaunchState:\s+(\w+)/) || [])[1] || null,
    });
    await (await this.driver.$(byId('role_selection'))).waitForDisplayed({ timeout: TIMEOUT });
    await sleep(1500);
  }
  this.coldStart = runs;
});

When('I start profiling {kraken-string}', async function (scenario) {
  const serial = this.device.id;
  const facts = deviceFacts(serial);
  const pid = await waitForPid(serial);
  // A bigger log buffer so the OkHttp lines of a whole scenario fit.
  tryShell(serial, 'logcat -G 16M');
  tryShell(serial, 'logcat -b all -c');
  tryShell(serial, `dumpsys gfxinfo ${PKG} reset`);
  const start = parseStat(shell(serial, statCommand(pid)));
  this.profile = {
    scenario, serial, pid, facts, start,
    startedAt: Date.now(),
    timings: {},
    checkpoints: [],
    notes: [],
  };
  this.profile.sampler = startSampler(this.profile);
});

// A scenario for a screen that one of the builds does not have yet is reported as skipped there.
Given('I skip this scenario on the build {kraken-string}', function (build) {
  return build === BUILD ? 'skipped' : undefined;
});

async function tapAndMeasure(world, tapSelector, targetSelector, label) {
  assert.ok(world.profile, 'Run "I start profiling" first');
  const tap = await world.driver.$(tapSelector);
  await tap.waitForDisplayed({ timeout: TIMEOUT });
  const t0 = Date.now();
  await tap.click();
  const target = await world.driver.$(targetSelector);
  await target.waitForDisplayed({ timeout: TIMEOUT, interval: POLL_MS });
  const ms = Date.now() - t0;
  // One query to an element already on screen: the Appium round trip that the time above includes.
  const r0 = Date.now();
  await target.isDisplayed();
  world.profile.timings[label] = { ms, appiumRoundTripMs: Date.now() - r0 };
}

When('I tap the element with id {kraken-string} and measure until I see the element with id {kraken-string} as {kraken-string}',
  async function (tapId, targetId, label) {
    await tapAndMeasure(this, byId(tapId), byId(targetId), label);
  });

When('I tap the element with id {kraken-string} and measure until I see a text containing {kraken-string} as {kraken-string}',
  async function (tapId, text, label) {
    await tapAndMeasure(this, byId(tapId), byTextContaining(text), label);
  });

When('I tap the first element with id starting with {kraken-string} and measure until I see the element with id {kraken-string} as {kraken-string}',
  async function (prefix, targetId, label) {
    await tapAndMeasure(this, byIdPrefix(prefix), byId(targetId), label);
  });

// Requests after a checkpoint are reported apart, e.g. those made when a screen is opened again.
When('I mark the network checkpoint {kraken-string}', function (name) {
  assert.ok(this.profile, 'Run "I start profiling" first');
  this.profile.checkpoints.push({ name, requestsBefore: readNetwork(this.profile).requests.length });
});

When('I swipe up {int} times in the element with id {kraken-string}', async function (times, id) {
  const el = await this.driver.$(byId(id));
  await el.waitForDisplayed({ timeout: TIMEOUT });
  const { x, y } = await el.getLocation();
  const { width, height } = await el.getSize();
  const cx = Math.round(x + width / 2);
  const from = Math.round(y + height * 0.8);
  const to = Math.round(y + height * 0.25);
  for (let i = 0; i < times; i++) {
    await this.driver.performActions([{
      type: 'pointer',
      id: 'finger',
      parameters: { pointerType: 'touch' },
      actions: [
        { type: 'pointerMove', duration: 0, x: cx, y: from },
        { type: 'pointerDown', button: 0 },
        { type: 'pause', duration: 50 },
        { type: 'pointerMove', duration: 250, x: cx, y: to },
        { type: 'pointerUp', button: 0 },
      ],
    }]);
    await this.driver.releaseActions();
    await sleep(700);
  }
});

// For controls that only exist with some data, like «Leer más» when the description is long.
When('I tap the element with id {kraken-string} if it is shown', async function (id) {
  const el = await this.driver.$(byId(id));
  const shown = await el.waitForDisplayed({ timeout: 3000 }).catch(() => false);
  if (shown) await el.click();
  if (this.profile) this.profile.notes.push(`${id}: ${shown ? 'tocado' : 'no estaba en pantalla'}`);
});

Then('I save the profiling', async function () {
  const p = this.profile;
  assert.ok(p, 'Run "I start profiling" first');
  p.sampler.running = false;
  await p.sampler.done;
  const durationMs = Date.now() - p.startedAt;
  const end = parseStat(shell(p.serial, statCommand(p.pid)));
  const meminfo = parseMeminfo(shell(p.serial, `dumpsys meminfo ${PKG}`));
  const frames = parseGfxinfo(shell(p.serial, `dumpsys gfxinfo ${PKG}`));
  const network = readNetwork(p);
  const anr = tryShell(p.serial, 'logcat -d -b events -s am_anr').split('\n').filter((l) => l.includes(PKG)).length;
  const crashes = tryShell(p.serial, 'logcat -d -b crash').split('\n').filter((l) => l.includes(`Process: ${PKG}`)).length;

  const ms = (ticks) => Math.round((ticks / p.facts.clkTck) * 1000);
  const totalTicks = end.total - p.start.total;
  const mainTicks = end.main - p.start.main;
  const cpuSamples = p.sampler.samples.filter((s) => s.cpuPct !== undefined).map((s) => s.cpuPct);
  const segments = p.checkpoints.map((cp, i) => {
    const until = i + 1 < p.checkpoints.length ? p.checkpoints[i + 1].requestsBefore : network.requests.length;
    return { name: cp.name, requests: network.requests.slice(cp.requestsBefore, until) };
  });

  const result = {
    scenario: p.scenario,
    build: BUILD,
    run: RUN,
    device: { manufacturer: p.facts.manufacturer, model: p.facts.model, androidRelease: p.facts.androidRelease, sdk: p.facts.sdk },
    recordedAt: new Date(p.startedAt).toISOString(),
    durationMs,
    coldStart: this.coldStart || null,
    timings: p.timings,
    cpu: {
      cores: p.facts.cores,
      totalMs: ms(totalTicks),
      mainThreadMs: ms(mainTicks),
      otherThreadsMs: ms(totalTicks - mainTicks),
      // Share of the whole device (all cores), like the Android Studio Profiler.
      avgPct: Math.round(((totalTicks / p.facts.clkTck) / (durationMs / 1000) / p.facts.cores) * 1000) / 10,
      peakPct: cpuSamples.length ? Math.round(Math.max(...cpuSamples) * 10) / 10 : null,
    },
    memory: {
      ...meminfo,
      peakRssKb: Math.max(end.rssKb || 0, ...p.sampler.samples.map((s) => s.rssKb || 0)),
      peakThreads: Math.max(end.threads || 0, ...p.sampler.samples.map((s) => s.threads || 0)),
    },
    frames,
    network: {
      apiRequests: network.requests.length,
      requests: network.requests,
      responses: network.responses,
      segments,
    },
    anr,
    crashes,
    notes: p.notes,
  };

  const dir = deviceDir(p.facts);
  fs.mkdirSync(dir, { recursive: true });
  fs.writeFileSync(path.join(dir, `${p.scenario}-run${RUN}.json`), `${JSON.stringify(result, null, 2)}\n`);
  assert.strictEqual(crashes, 0, `${PKG} crashed during ${p.scenario}`);
});
