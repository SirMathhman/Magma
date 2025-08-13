const compile = require('./compile');

describe('compile Magma to C', () => {
  it('compiles U8 literal declaration', () => {
    expect(compile('let x = 0U8;')).toBe('uint8_t x = 0;');
  });
  it('compiles U16 literal declaration', () => {
    expect(compile('let x = 0U16;')).toBe('uint16_t x = 0;');
  });
  it('compiles U32 literal declaration', () => {
    expect(compile('let x = 0U32;')).toBe('uint32_t x = 0;');
  });
  it('compiles U64 literal declaration', () => {
    expect(compile('let x = 0U64;')).toBe('uint64_t x = 0;');
  });
  it('compiles I8 literal declaration', () => {
    expect(compile('let x = 0I8;')).toBe('int8_t x = 0;');
  });
  it('compiles I16 literal declaration', () => {
    expect(compile('let x = 0I16;')).toBe('int16_t x = 0;');
  });
  it('compiles I32 literal declaration', () => {
    expect(compile('let x = 0I32;')).toBe('int32_t x = 0;');
  });
  it('compiles I64 literal declaration', () => {
    expect(compile('let x = 0I64;')).toBe('int64_t x = 0;');
  });
  const typeCases = [
    // [input, expected]
    ['let x = 0U8;', 'uint8_t x = 0;'],
    ['let x = 0U16;', 'uint16_t x = 0;'],
    ['let x = 0U32;', 'uint32_t x = 0;'],
    ['let x = 0U64;', 'uint64_t x = 0;'],
    ['let x = 0I8;', 'int8_t x = 0;'],
    ['let x = 0I16;', 'int16_t x = 0;'],
    ['let x = 0I32;', 'int32_t x = 0;'],
    ['let x = 0I64;', 'int64_t x = 0;'],
    ['let x : U8 = 0;', 'uint8_t x = 0;'],
    ['let x : U16 = 0;', 'uint16_t x = 0;'],
    ['let x : U32 = 0;', 'uint32_t x = 0;'],
    ['let x : U64 = 0;', 'uint64_t x = 0;'],
    ['let x : I8 = 0;', 'int8_t x = 0;'],
    ['let x : I16 = 0;', 'int16_t x = 0;'],
    ['let x : I32 = 0;', 'int32_t x = 0;'],
    ['let x : I64 = 0;', 'int64_t x = 0;'],
    ['let x = 0;', 'int32_t x = 0;'],
  ];

  test.each(typeCases)('compiles "%s" to "%s"', (input, expected) => {
    expect(compile(input)).toBe(expected);
  });
});