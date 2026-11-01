const { When, Then } = require('@cucumber/cucumber');
const assert = require('assert');

// Compose exposes testTags as resource ids (testTagsAsResourceId in MainScreen).
const byText = (text) => `android=new UiSelector().text("${text}")`;
const byId = (id) => `android=new UiSelector().resourceId("${id}")`;
// Kraken does not scroll by itself: this scrolls the list with that id until the text is on screen.
const byTextInScrollable = (containerId, text) =>
  `android=new UiScrollable(new UiSelector().resourceId("${containerId}")).scrollIntoView(new UiSelector().text("${text}"))`;
const TIMEOUT = 15000;

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

When('I type {kraken-string} into the element with id {kraken-string}', async function (value, id) {
  const el = await this.driver.$(byId(id));
  await el.waitForDisplayed({ timeout: TIMEOUT });
  await el.setValue(value);
});

When('I scroll to the text {kraken-string} in the element with id {kraken-string}', async function (text, id) {
  await (await this.driver.$(byId(id))).waitForDisplayed({ timeout: TIMEOUT });
  const el = await this.driver.$(byTextInScrollable(id, text));
  await el.waitForDisplayed({ timeout: TIMEOUT });
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
