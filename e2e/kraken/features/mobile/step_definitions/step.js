const { When, Then } = require('@cucumber/cucumber');
const assert = require('assert');
const { execFileSync } = require('child_process');

// Compose exposes testTags as resource ids (testTagsAsResourceId in MainScreen).
const byText = (text) => `android=new UiSelector().text("${text}")`;
const byId = (id) => `android=new UiSelector().resourceId("${id}")`;
// Kraken does not scroll by itself: this scrolls the list with that id until the text is on screen.
const byTextInScrollable = (containerId, text) =>
  `android=new UiScrollable(new UiSelector().resourceId("${containerId}")).scrollIntoView(new UiSelector().text("${text}"))`;
const TIMEOUT = 15000;
const SCROLL_TIMEOUT = 5000;
const MAX_SWIPES = 12;

// Scrolls the container until the target selector is on screen. UiScrollable goes first; on Android 8
// (API 26) it often stops after one swipe on Compose lists, so it falls back to real finger swipes
// with `adb shell input swipe` (Appium 1.22 does not take W3C actions) inside the container.
async function scrollUntilDisplayed(world, containerId, scrollableSelector, targetSelector) {
  const container = await world.driver.$(byId(containerId));
  await container.waitForDisplayed({ timeout: TIMEOUT });
  try {
    await (await world.driver.$(scrollableSelector)).waitForDisplayed({ timeout: SCROLL_TIMEOUT });
    return;
  } catch (e) {
    // Not reached by UiScrollable: swipe by hand below.
  }
  const { x, y } = await container.getLocation();
  const { width, height } = await container.getSize();
  const cx = Math.round(x + width / 2);
  const from = Math.round(y + height * 0.8);
  const to = Math.round(y + height * 0.3);
  for (let i = 0; i < MAX_SWIPES; i++) {
    if (await (await world.driver.$(targetSelector)).isDisplayed()) return;
    execFileSync('adb', ['-s', world.device.id, 'shell', `input swipe ${cx} ${from} ${cx} ${to} 400`]);
    await new Promise((resolve) => setTimeout(resolve, 700));
  }
  await (await world.driver.$(targetSelector)).waitForDisplayed({ timeout: TIMEOUT });
}

When('I tap the text {kraken-string}', async function (text) {
  const el = await this.driver.$(byText(text));
  await el.waitForDisplayed({ timeout: TIMEOUT });
  await el.click();
});

When('I tap the element with id {kraken-string}', async function (id) {
  const el = await this.driver.$(byId(id));
  await el.waitForDisplayed({ timeout: TIMEOUT });
  await el.click();
});

// For list rows whose id depends on the backend (collector_row_<id>): taps the first one on screen.
When('I tap the first element with id starting with {kraken-string}', async function (prefix) {
  const el = await this.driver.$(`android=new UiSelector().resourceIdMatches("${prefix}.*")`);
  await el.waitForDisplayed({ timeout: TIMEOUT });
  await el.click();
});

When('I type {kraken-string} into the element with id {kraken-string}', async function (value, id) {
  const el = await this.driver.$(byId(id));
  await el.waitForDisplayed({ timeout: TIMEOUT });
  await el.setValue(value);
});

When('I scroll to the text {kraken-string} in the element with id {kraken-string}', async function (text, id) {
  await scrollUntilDisplayed(this, id, byTextInScrollable(id, text), byText(text));
});

// Same as above, but finds the item by its id (testTag). Use it for items whose text changes between
// backends, like the seed album: its id (album_item_100) is the same everywhere, its name is not.
When('I scroll to the element with id {kraken-string} in the element with id {kraken-string}', async function (itemId, containerId) {
  await scrollUntilDisplayed(
    this,
    containerId,
    `android=new UiScrollable(new UiSelector().resourceId("${containerId}")).scrollIntoView(new UiSelector().resourceId("${itemId}"))`,
    byId(itemId),
  );
});

When('I hide the keyboard', async function () {
  if (await this.driver.isKeyboardShown()) {
    await this.driver.hideKeyboard();
  }
});

Then('I should see the text {kraken-string}', async function (text) {
  assert.ok(await (await this.driver.$(byText(text))).waitForDisplayed({ timeout: TIMEOUT }));
});

// "Not see" passes at once if the element was never drawn or is off-screen.
// Use it only after checking that the element was on screen, or after a positive check of the new state.
Then('I should not see the text {kraken-string}', async function (text) {
  assert.ok(await (await this.driver.$(byText(text))).waitForDisplayed({ timeout: TIMEOUT, reverse: true }));
});

Then('I should see the element with id {kraken-string}', async function (id) {
  assert.ok(await (await this.driver.$(byId(id))).waitForDisplayed({ timeout: TIMEOUT }));
});

Then('I should not see the element with id {kraken-string}', async function (id) {
  assert.ok(await (await this.driver.$(byId(id))).waitForDisplayed({ timeout: TIMEOUT, reverse: true }));
});

Then('the element with id {kraken-string} should be selected', async function (id) {
  const el = await this.driver.$(byId(id));
  await el.waitForDisplayed({ timeout: TIMEOUT });
  await this.driver.waitUntil(async () => el.isSelected(), { timeout: TIMEOUT, timeoutMsg: `${id} is not selected` });
});
