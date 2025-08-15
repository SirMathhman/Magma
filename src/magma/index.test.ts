import { compile } from './index';

test('empty', () => {
  expect(compile('')).toBe('');
});

test('let x and y copy', () => {
  const src = 'let x = 20; let y = x;';
  expect(compile(src)).toBe('#include <stdint.h>\r\nint32_t x = 20;\r\nint32_t y = x;');
});

test('bool', () => {
  expect(compile('let b : Bool = true;')).toBe('#include <stdbool.h>\r\nbool b = true;');
});
