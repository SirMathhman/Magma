"use strict";

const { always_throws } = require('../src/always_throws');

test('empty input returns "empty" string', () => {
  expect(always_throws('')).toBe('empty');
});

test('non-empty input throws Error with message', () => {
  expect(() => always_throws('not empty')).toThrow('Input must be empty');
});

test('non-string input throws TypeError', () => {
  expect(() => always_throws(123)).toThrow(TypeError);
});
