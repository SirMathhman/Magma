const compile = require('./compile');

describe('compile Magma to C', () => {
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