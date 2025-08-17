const assert = require('assert');
const path = require('path');
const alwaysThrows = require(path.join(__dirname, '..', 'lib', 'alwaysThrows'));

let threw = false;
try {
  alwaysThrows();
} catch (e) {
  threw = true;
  assert.strictEqual(e.message, 'This function always throws');
}

assert.ok(threw, 'alwaysThrows did not throw');
console.log('PASS');
