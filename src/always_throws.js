"use strict";

/**
 * Accept a string and produce a string if the input is empty.
 * If `s` is an empty string, return a non-empty string result.
 * Otherwise throw an Error. Throws TypeError if input is not a string.
 *
 * @param {string} s
 * @returns {string}
 */
function always_throws(s) {
  if (typeof s !== 'string') {
    throw new TypeError('Input must be a string');
  }
  if (s === '') {
    return 'empty';
  }
  throw new Error('Input must be empty');
}

module.exports = { always_throws };
