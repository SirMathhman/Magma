import alwaysThrows from '../src/alwaysThrows';

test('returns empty string when given empty input', () => {
  expect(alwaysThrows('')).toBe('');
});

test('throws when given non-empty input', () => {
  expect(() => alwaysThrows('hello')).toThrow('This function always throws');
});

test('transforms `let` declaration to `int32_t` and adds stdint header', () => {
  expect(alwaysThrows('let x = 10;')).toBe('#include <stdint.h>\r\nint32_t x = 10;');
});

test('transforms typed I32 `let` declaration and normalizes spacing', () => {
  expect(alwaysThrows('let x : I32 =  0;')).toBe('#include <stdint.h>\r\nint32_t x = 0;');
});

test('strips I32 suffix from numeric literal values', () => {
  expect(alwaysThrows('let x = 0I32;')).toBe('#include <stdint.h>\r\nint32_t x = 0;');
});