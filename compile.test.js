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
    expect(() => compile(input)).toThrow();
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
    // Bool type tests
    ['let x = true;', 'bool x = true;'],
    ['let x = false;', 'bool x = false;'],
    ['let x : Bool = true;', 'bool x = true;'],
    ['let x : Bool = false;', 'bool x = false;'],
    ['let x = trueBool;', 'bool x = true;'],
    ['let x = falseBool;', 'bool x = false;'],
    ['let x : Bool = trueBool;', 'bool x = true;'],
    ['let x : Bool = falseBool;', 'bool x = false;'],
  ];

  test.each(typeCases)('compiles "%s" to "%s"', (input, expected) => {
    expect(compile(input)).toBe(expected);
  });

  test('throws on non-bool value for Bool type', () => {
    expect(() => compile('let x : Bool = 0;')).toThrow();
  });

  test('compiles chained assignment', () => {
    expect(compile('let x = 0; let y = x;')).toBe('int32_t x = 0; int32_t y = x;');
  });

  // Array tests
  test('compiles array declaration', () => {
    expect(compile('let x : [U8; 3] = [1, 2, 3];')).toBe('uint8_t x[3] = {1, 2, 3};');
    expect(compile('let y : [I32; 2] = [10, -5];')).toBe('int32_t y[2] = {10, -5};');
  });
  test('throws on array length mismatch', () => {
    expect(() => compile('let x : [U8; 2] = [1, 2, 3];')).toThrow();
  });
  test('throws on unsupported array element type', () => {
    expect(() => compile('let x : [Foo; 2] = [1, 2];')).toThrow();
  });
  test('throws on non-integer array element', () => {
    expect(() => compile('let x : [U8; 2] = [1, true];')).toThrow();
  });
});