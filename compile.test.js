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
  it('compiles U8 variable declaration', () => {
    expect(compile('let x : U8 = 0;')).toBe('uint8_t x = 0;');
  });
  it('compiles U16 variable declaration', () => {
    expect(compile('let x : U16 = 0;')).toBe('uint16_t x = 0;');
  });
  it('compiles U32 variable declaration', () => {
    expect(compile('let x : U32 = 0;')).toBe('uint32_t x = 0;');
  });
  it('compiles U64 variable declaration', () => {
    expect(compile('let x : U64 = 0;')).toBe('uint64_t x = 0;');
  });
  it('compiles I8 variable declaration', () => {
    expect(compile('let x : I8 = 0;')).toBe('int8_t x = 0;');
  });
  it('compiles I16 variable declaration', () => {
    expect(compile('let x : I16 = 0;')).toBe('int16_t x = 0;');
  });
  it('compiles I32 variable declaration', () => {
    expect(compile('let x : I32 = 0;')).toBe('int32_t x = 0;');
  });
  it('compiles I64 variable declaration', () => {
    expect(compile('let x : I64 = 0;')).toBe('int64_t x = 0;');
  });
  it('should compile Magma variable declaration to C', () => {
    expect(compile('let x : I32 = 0;')).toBe('int32_t x = 0;');
  });

  it('should compile JS let to C int32_t', () => {
    expect(compile('let x = 0;')).toBe('int32_t x = 0;');
  });
});