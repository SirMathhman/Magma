import compileMagmaToC from '../src/compiler';

test('compile simple let: let x : I32 = 0;', () => {
  const src = 'let x : I32 = 0;';
  const out = compileMagmaToC(src);
  expect(out).toContain('#include <stdint.h>');
  expect(out).toContain('int32_t x = 0;');
  expect(out).toMatch(/int main\s*\(/);
});
