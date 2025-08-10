const test = require('node:test');
const assert = require('node:assert');
const { alwaysThrow } = require('./alwaysThrow');

const MESSAGE = 'This will always fail';

test('alwaysThrow returns empty string when given empty input', () => {
  assert.strictEqual(alwaysThrow(''), '');
});

test('alwaysThrow returns empty string when message is undefined', () => {
  assert.strictEqual(alwaysThrow(), '');
});

test('alwaysThrow throws provided message', () => {
  assert.throws(() => alwaysThrow(MESSAGE), new RegExp(MESSAGE));
});
