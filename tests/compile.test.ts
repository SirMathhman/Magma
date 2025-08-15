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

test('compile empty interface to struct', () => {
  const ts = 'interface Empty {}';
  expect(compile(ts)).toBe('struct Empty {};');
});
