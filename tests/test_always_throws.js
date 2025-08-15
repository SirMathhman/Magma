"use strict";

const assert = require('assert');
const path = require('path');
const { always_throws } = require(path.join('..', 'src', 'always_throws'));

try {
  // Empty input should return the sentinel string
  const result = always_throws('');
  assert.strictEqual(typeof result, 'string');
  assert.strictEqual(result, 'empty');

  // Non-empty input should throw an Error with the expected message
  let didThrow = false;
  try {
    always_throws('not empty');
  } catch (err) {
    didThrow = true;
    assert.strictEqual(err.message, 'Input must be empty');
  }
  assert.ok(didThrow, 'always_throws should have thrown for non-empty input');

  console.log('All tests passed');
  process.exit(0);
} catch (err) {
  console.error('Test failure:', err && err.stack ? err.stack : err);
  process.exit(1);
}
