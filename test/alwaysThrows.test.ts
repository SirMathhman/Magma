import assert from 'assert';
import path from 'path';
import alwaysThrows from '../src/alwaysThrows';

let threw = false;
try {
  alwaysThrows();
} catch (e: any) {
  threw = true;
  assert.strictEqual(e.message, 'This function always throws');
}

assert.ok(threw, 'alwaysThrows did not throw');
console.log('PASS');
