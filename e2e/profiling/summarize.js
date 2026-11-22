#!/usr/bin/env node
'use strict';
// Reads results/<build>/<device>/*.json and writes results/summary.md with the median of the runs
// per scenario, and the before/after comparison for each device that has both builds.
const fs = require('fs');
const path = require('path');

const RESULTS_DIR = path.join(__dirname, 'results');
const SCENARIOS = ['P-ONB', 'P-HU01', 'P-HU02', 'P-HU03', 'P-HU04', 'P-HU05', 'P-HU06', 'P-HU07', 'P-HU08'];
const BUILDS = ['antes', 'despues'];

const median = (values) => {
  const xs = values.filter((v) => typeof v === 'number' && !Number.isNaN(v)).sort((a, b) => a - b);
  if (!xs.length) return null;
  const mid = Math.floor(xs.length / 2);
  return xs.length % 2 ? xs[mid] : (xs[mid - 1] + xs[mid]) / 2;
};
const mb = (kb) => (kb == null ? null : kb / 1024);
const fmt = (v, decimals = 0) => (v == null ? '—' : v.toLocaleString('es-CO', { maximumFractionDigits: decimals, minimumFractionDigits: decimals }));
const readJson = (file) => JSON.parse(fs.readFileSync(file, 'utf8'));
const dirs = (dir) => (fs.existsSync(dir) ? fs.readdirSync(dir).filter((d) => fs.statSync(path.join(dir, d)).isDirectory()) : []);

// Metrics of one run of a scenario: [label, value, decimals]. Timings and network segments depend on the scenario.
function metrics(r) {
  const rows = [];
  if (r.coldStart) rows.push(['Arranque en frío hasta el primer frame (ms, `am start -W`)', median(r.coldStart.map((c) => c.totalTimeMs)), 0]);
  for (const [label, t] of Object.entries(r.timings || {})) {
    rows.push([`Tiempo hasta ver «${label}» (ms)`, t.ms, 0]);
  }
  rows.push(
    ['CPU promedio del recorrido (% del dispositivo)', r.cpu.avgPct, 1],
    ['CPU pico en ventanas de 0,5 s (% del dispositivo)', r.cpu.peakPct, 1],
    ['Tiempo de CPU en el hilo `main` (ms)', r.cpu.mainThreadMs, 0],
    ['Tiempo de CPU en los demás hilos (ms)', r.cpu.otherThreadsMs, 0],
    ['Memoria total PSS al final (MB)', mb(r.memory.totalPssKb), 1],
    ['Java/Kotlin heap al final (MB)', mb(r.memory.javaHeapKb), 1],
    ['Native heap al final (MB)', mb(r.memory.nativeHeapKb), 1],
    ['Gráficos al final (MB)', mb(r.memory.graphicsKb), 1],
    ['RSS pico (MB)', mb(r.memory.peakRssKb), 1],
    ['Hilos vivos (pico)', r.memory.peakThreads, 0],
    ['Activities vivas al final', r.memory.activities, 0],
    ['Frames dibujados', r.frames.total, 0],
    ['Frames lentos (> 16 ms)', r.frames.slowOver16ms, 0],
    ['Frames congelados (> 700 ms)', r.frames.frozenOver700ms, 0],
    ['Frames con jank (%)', r.frames.jankyPct, 1],
    ['Frame p90 (ms)', r.frames.p90Ms, 0],
    ['Frame p99 (ms)', r.frames.p99Ms, 0],
    ['Peticiones a la API en todo el recorrido', r.network.apiRequests, 0],
  );
  for (const s of r.network.segments || []) {
    rows.push([`Peticiones a la API desde «${s.name}»`, s.requests.length, 0]);
  }
  rows.push(['ANR', r.anr, 0], ['Cierres inesperados', r.crashes, 0]);
  return rows;
}

// { label: { values: [...], decimals } } over all the runs of a scenario.
function aggregate(runs) {
  const table = new Map();
  for (const run of runs) {
    for (const [label, value, decimals] of metrics(run)) {
      if (!table.has(label)) table.set(label, { values: [], decimals });
      table.get(label).values.push(value);
    }
  }
  return table;
}

function load() {
  const data = {}; // data[device][build][scenario] = [runs]
  const devices = {};
  for (const build of dirs(RESULTS_DIR)) {
    for (const device of dirs(path.join(RESULTS_DIR, build))) {
      const dir = path.join(RESULTS_DIR, build, device);
      for (const file of fs.readdirSync(dir)) {
        if (!file.endsWith('.json')) continue;
        if (file === 'device.json') {
          devices[device] = readJson(path.join(dir, file));
          continue;
        }
        const r = readJson(path.join(dir, file));
        ((data[device] ??= {})[build] ??= {})[r.scenario] ??= [];
        data[device][build][r.scenario].push(r);
      }
    }
  }
  return { data, devices };
}

function requestList(runs, segmentName) {
  const paths = new Map();
  for (const r of runs) {
    const requests = segmentName
      ? (r.network.segments.find((s) => s.name === segmentName) || { requests: [] }).requests
      : r.network.requests;
    for (const q of requests) paths.set(`${q.method} ${q.path}`, (paths.get(`${q.method} ${q.path}`) || 0) + 1);
  }
  return [...paths.entries()].map(([p, n]) => `\`${p}\` ×${fmt(n / runs.length, 1)}`).join(', ') || 'ninguna';
}

function main() {
  const { data, devices } = load();
  const out = [`# Resultados de perfilamiento`, '', `Generado el ${new Date().toISOString().slice(0, 16).replace('T', ' ')} UTC con \`e2e/profiling/summarize.js\`. Cada valor es la **mediana** de las corridas.`, ''];

  out.push('## Dispositivos', '', '| Modelo | Android (API) | RAM | SoC | Núcleos · frec. máx. | Pantalla |', '|---|---|---|---|---|---|');
  for (const [id, d] of Object.entries(devices)) {
    out.push(`| ${[d.manufacturer, d.marketName || d.model].filter(Boolean).join(' ')} (${d.model}) | ${d.androidRelease} (API ${d.sdk}) | ${fmt(d.ramGb, 1)} GB | ${d.soc || '—'} | ${d.cores} · ${fmt(d.cpuMaxGhz, 2)} GHz | ${d.screen} @ ${d.density} dpi |`);
  }
  out.push('');

  for (const [device, builds] of Object.entries(data)) {
    for (const build of BUILDS.filter((b) => builds[b])) {
      out.push(`## ${device} · ${build}`, '');
      for (const scenario of SCENARIOS.filter((s) => builds[build][s])) {
        const runs = builds[build][scenario];
        out.push(`### ${scenario} (${runs.length} corridas)`, '', '| Métrica | Mediana | Mín. | Máx. |', '|---|---|---|---|');
        for (const [label, { values, decimals }] of aggregate(runs)) {
          const xs = values.filter((v) => v != null);
          out.push(`| ${label} | ${fmt(median(xs), decimals)} | ${fmt(xs.length ? Math.min(...xs) : null, decimals)} | ${fmt(xs.length ? Math.max(...xs) : null, decimals)} |`);
        }
        const rtt = median(runs.flatMap((r) => Object.values(r.timings || {}).map((t) => t.appiumRoundTripMs)));
        out.push('', `Peticiones por corrida: ${requestList(runs)}.`);
        if (rtt != null) out.push(`Los tiempos incluyen la ida y vuelta de Appium (mediana ${fmt(rtt)} ms por consulta).`);
        out.push('');
      }
    }

    if (builds.antes && builds.despues) {
      out.push(`## ${device} · antes vs. después`, '', '| Escenario | Métrica | Antes | Después | Cambio |', '|---|---|---|---|---|');
      for (const scenario of SCENARIOS.filter((s) => builds.antes[s] && builds.despues[s])) {
        const before = aggregate(builds.antes[scenario]);
        const after = aggregate(builds.despues[scenario]);
        for (const [label, { values, decimals }] of after) {
          if (!before.has(label)) continue;
          const a = median(before.get(label).values);
          const d = median(values);
          const change = a ? `${d >= a ? '+' : ''}${fmt(((d - a) / a) * 100, 0)} %` : (a === d ? '=' : '—');
          out.push(`| ${scenario} | ${label} | ${fmt(a, decimals)} | ${fmt(d, decimals)} | ${change} |`);
        }
      }
      out.push('');
    }
  }

  fs.mkdirSync(RESULTS_DIR, { recursive: true });
  fs.writeFileSync(path.join(RESULTS_DIR, 'summary.md'), `${out.join('\n')}\n`);
  console.log(`Resumen en ${path.join(RESULTS_DIR, 'summary.md')}`);
}

main();
