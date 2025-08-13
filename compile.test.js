const { compile } = require('./compile');

describe('compile Magma to C', () => {
  test('compiles function declaration with multiple parameters', () => {
    expect(compile('fn multi(a : I32, b : U8, c : Bool): Void => {}')).toBe('void multi(int32_t a, uint8_t b, bool c) {}');
    expect(compile('fn foo(x : U16, y : I8): Void => {}')).toBe('void foo(uint16_t x, int8_t y) {}');
  });

  test('compiles empty function declaration', () => {
    expect(compile('fn empty() : Void => {}')).toBe('void empty() {}');
  });

  test('compiles function declaration with one parameter', () => {
    expect(compile('fn once(value : I32): Void => {}')).toBe('void once(int32_t value) {}');
  });
  test('compiles basic addition', () => {
    expect(compile('let x = 5 + 3;')).toBe('int32_t x = 5 + 3;');
  });

  test('compiles basic subtraction', () => {
    expect(compile('let x = 10 - 2;')).toBe('int32_t x = 10 - 2;');
  });

  test('compiles basic multiplication', () => {
    expect(compile('let x = 4 * 7;')).toBe('int32_t x = 4 * 7;');
  });

  test('compiles basic division', () => {
    expect(compile('let x = 20 / 5;')).toBe('int32_t x = 20 / 5;');
  });

  test('compiles mixed arithmetic', () => {
    expect(compile('let x = 2 + 3 * 4 - 5 / 2;')).toBe('int32_t x = 2 + 3 * 4 - 5 / 2;');
  });
  test('compiles basic while statement', () => {
    expect(compile('while(true){}')).toBe('while(true){}');
  });

  test('compiles while with assignment', () => {
    expect(compile('let mut x = 0; while(x < 10){x = x + 1;}')).toBe('int32_t x = 0; while(x < 10){x = x + 1;}');
  });

  test('compiles nested while statements', () => {
    expect(compile('while(true){while(false){}}')).toBe('while(true){while(false){}}');
  });

  test('compiles while with empty block', () => {
    expect(compile('while(false){}')).toBe('while(false){}');
  });

  test('compiles while with complex condition', () => {
    expect(compile('while(x > 0 && y < 5){}')).toBe('while(x > 0 && y < 5){}');
  });
  test('let inside if block is scoped', () => {
    expect(compile('if(true){let x = 1;}')).toBe('if(true){int32_t x = 1;}');
  });

  test('let outside if block is global', () => {
    expect(compile('let mut x = 2; if(true){x = 3;}')).toBe('int32_t x = 2; if(true){x = 3;}');
  });

  test('let in both global and if block scopes', () => {
    expect(compile('let x = 4; if(true){let x = 5;}')).toBe('int32_t x = 4; if(true){int32_t x = 5;}');
  });

  test('let in else block', () => {
    expect(compile('if(false){}else{let y = 6;}')).toBe('if(false){}else{int32_t y = 6;}');
  });
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

  test('compiles single-quoted character as U8', () => {
    expect(compile("let x = 'A';")).toBe("uint8_t x = 'A';");
    expect(compile("let mut x = 'B'; x = 'C';")).toBe("uint8_t x = 'B'; x = 'C';");
  });

  test('compiles string literal to finite-sized U8 array', () => {
    expect(compile('let x = "abc";')).toBe("uint8_t x[3] = {'a', 'b', 'c'};");
    expect(compile('let x : [U8; 3] = "xyz";')).toBe("uint8_t x[3] = {'x', 'y', 'z'};");
    expect(() => compile('let x : [U8; 2] = "abc";')).toThrow();
  });

  test('compiles mutable assignment', () => {
    expect(compile('let mut x = 200; x = 100;')).toBe('int32_t x = 200; x = 100;');
  });

  test('throws on assignment to immutable variable', () => {
    expect(() => compile('let x = 200; x = 100;')).toThrow();
  });

  // Array tests
  test('compiles array declaration', () => {
    expect(compile('let x : [U8; 3] = [1, 2, 3];')).toBe('uint8_t x[3] = {1, 2, 3};');
    expect(compile('let y : [I32; 2] = [10, -5];')).toBe('int32_t y[2] = {10, -5};');
  });

  test('compiles empty braces', () => {
    expect(compile('{}')).toBe('{}');
  });

  test('compiles block syntax', () => {
    expect(compile('{let x = 100;}')).toBe('{int32_t x = 100;}');
  });

  test('compiles statement followed by empty block', () => {
    expect(compile('let x = 100; {}')).toBe('int32_t x = 100; {}');
  });

  test('compiles empty block followed by statement', () => {
    expect(compile('{} let x = 100;')).toBe('{} int32_t x = 100;');
  });

  test('compiles block after statement to valid C', () => {
    expect(compile('let x = 10; {let y = x;}')).toBe('int32_t x = 10; {int32_t y = x;}');
  });

  test('throws when accessing block-local variable outside block', () => {
    expect(() => compile('{let x = 200;} let y = x;')).toThrow();
  });

  test('multi-dimensional arrays are not supported', () => {
    expect(() => compile('let x : [[U8; 2]; 2] = [[1, 2], [3, 4]];')).toThrow();
    expect(() => compile('let x : [[I32; 2]; 2] = [[10, 20], [30, 40]];')).toThrow();
    expect(() => compile('let x : [U8; 2, 2] = [[1, 2], [3, 4]];')).toThrow();
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

  test('compiles equality expression', () => {
    expect(compile('a == b')).toBe('a == b');
  });
  test('compiles not-equal expression', () => {
    expect(compile('x != y')).toBe('x != y');
  });

  test('compiles less-than expression', () => {
    expect(compile('x < y')).toBe('x < y');
  });

  test('compiles greater-than expression', () => {
    expect(compile('x > y')).toBe('x > y');
  });

  test('compiles less-than-or-equal expression', () => {
    expect(compile('x <= y')).toBe('x <= y');
  });

  test('compiles greater-than-or-equal expression', () => {
    expect(compile('x >= y')).toBe('x >= y');
  });

  test('compiles literal comparison', () => {
    expect(compile('5 < 10')).toBe('5 < 10');
    expect(compile('5 > 10')).toBe('5 > 10');
    expect(compile('5 == 10')).toBe('5 == 10');
    expect(compile('5 != 10')).toBe('5 != 10');
    expect(compile('5 <= 10')).toBe('5 <= 10');
    expect(compile('5 >= 10')).toBe('5 >= 10');
  });

  test('compiles basic if-else statement', () => {
    expect(compile('if(true){}else{}')).toBe('if(true){}else{}');
  });

  test('compiles basic if statement', () => {
    expect(compile('if(true){}')).toBe('if(true){}');
  });
  test('compiles nested if statements', () => {
    expect(compile('if(true){if(false){}}')).toBe('if(true){if(false){}}');
  });

  test('compiles if-else with empty blocks', () => {
    expect(compile('if(false){}else{}')).toBe('if(false){}else{}');
  });

  test('compiles if statement with complex condition', () => {
    expect(compile('if(5 > 3 && 2 < 4){}')).toBe('if(5 > 3 && 2 < 4){}');
  });

  test('compiles if-else if-else chain', () => {
    expect(compile('let mut a = true; let mut b = false; if(a){}else if(b){}else{}')).toBe('bool a = true; bool b = false; if(a){}else if(b){}else{}');
  });
});