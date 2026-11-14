const { After, Before } = require('@cucumber/cucumber');
const { AndroidClient } = require('kraken-node');
const { execFileSync } = require('child_process');
const fs = require('fs');
const path = require('path');

const REPO_ROOT = path.resolve(__dirname, '../../../../..');

/**
 * Port of api.base.url when it points to this computer (http://localhost:<port>/), read the same way
 * Gradle does: local.properties first, then gradle.properties. Null when the app talks to a remote backend.
 */
function localBackendPort() {
  for (const file of ['local.properties', 'gradle.properties']) {
    try {
      const props = fs.readFileSync(path.join(REPO_ROOT, file), 'utf8');
      const url = props.match(/^api\.base\.url=(.*)$/m);
      if (!url) continue;
      const local = url[1].trim().match(/^http:\/\/(?:localhost|127\.0\.0\.1):(\d+)\//);
      return local ? local[1] : null;
    } catch (e) {
      // File missing: try the next one.
    }
  }
  return null;
}

Before(async function () {
  this.deviceClient = new AndroidClient(
    this.device.id, this.apkPath,
    this.apkPackage, this.apkLaunchActivity,
    {}, this.userId
  );
  this.driver = await this.deviceClient.startKrakenForUserId(this.userId);

  // A phone over USB reaches a local backend only through `adb reverse`, and Appium may restart the
  // adb server when it starts, which drops that mapping. Map the port again for this device. The app
  // opens on the role selection screen and calls the API only after a role is chosen, so this is in time.
  const port = localBackendPort();
  if (port) {
    execFileSync('adb', ['-s', this.device.id, 'reverse', `tcp:${port}`, `tcp:${port}`]);
  }
})

After(async function () {
  await this.deviceClient.stopKrakenForUserId(this.userId);
});
