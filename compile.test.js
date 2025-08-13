const compile = require('./compile');

describe('compile Magma to C', () => {
  const mismatchedTypeCases = [
    ['let x : I64 = 0U8;'],
    ['let x : I32 = 0U16;'],
    ['let x : U8 = 0I64;'],
    ['let x : U16 = 0I32;'],
    ['let x : I8 = 0U64;'],
    ['let x : U64 = 0I8;'],
    // Add more combinations as needed
  ];

  test.each(mismatchedTypeCases)('throws on mismatched types: "%s"', (input) => {
    expect(() => compile(input)).toThrow('Type mismatch between declared and literal type');
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
    ['let x : U8 = 0U8;', 'uint8_t x = 0;'],
    ['let x : U16 = 0U16;', 'uint16_t x = 0;'],
    ['let x : U32 = 0U32;', 'uint32_t x = 0;'],
    ['let x : U64 = 0U64;', 'uint64_t x = 0;'],
    ['let x : I8 = 0I8;', 'int8_t x = 0;'],
    ['let x : I16 = 0I16;', 'int16_t x = 0;'],
    ['let x : I32 = 0I32;', 'int32_t x = 0;'],
    ['let x : I64 = 0I64;', 'int64_t x = 0;'],
  ];

  test.each(typeCases)('compiles "%s" to "%s"', (input, expected) => {
    expect(compile(input)).toBe(expected);
  });
});