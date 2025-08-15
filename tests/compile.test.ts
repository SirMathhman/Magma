import { compile } from '../src/compile';

test('empty input returns "empty" string', () => {
  expect(compile('')).toBe('empty');
});

test('non-empty input throws Error with message', () => {
  expect(() => compile('not empty')).toThrow('Input must be empty');
});

test('non-string input throws TypeError', () => {
  // @ts-expect-error: testing runtime type checking
  expect(() => compile(123)).toThrow(TypeError);
});

test('compile simple empty void function to C', () => {
  const ts = 'function doNothing() : void {}';
  expect(compile(ts)).toBe('void doNothing(){}');
});

test('compile function without return type defaults to void', () => {
  const ts = 'function empty(){}';
  expect(compile(ts)).toBe('void empty(){}');
});

test('compile empty interface to struct', () => {
  const ts = 'interface Empty {}';
  expect(compile(ts)).toBe('struct Empty {};');
});

test('compile interface with number member to struct with int64_t and include', () => {
  const ts = 'interface Wrapper {value : number}';
  expect(compile(ts)).toBe('#include <stdint.h>\r\nstruct Wrapper {int64_t value;};');
});

test('compile interface with string member to struct with char*', () => {
  const ts = 'interface Wrapper {value : string}';
  expect(compile(ts)).toBe('struct Wrapper {char* value;};');
});

test('compile interface with multiple members to struct with includes and fields', () => {
  const ts = 'interface Pair { a : number; b : string }';
  expect(compile(ts)).toBe('#include <stdint.h>\r\nstruct Pair {int64_t a; char* b;};');
});
