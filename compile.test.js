const compile = require('./compile');

describe('alwaysThrows', () => {
  it('should return a string if input is empty', () => {
    expect(compile('')).toBe('Input was empty.');
  });

  it('should throw an error if input is not empty', () => {
    expect(() => compile('not empty')).toThrow('This function always throws an error unless the input is an empty string.');
  });
});
// Test for Magma variable declaration compilation
describe('compile Magma to C', () => {
  it('should compile Magma variable declaration to C', () => {
    expect(compile('let x : I32 = 0;')).toBe('int32_t x = 0;');
  });
});