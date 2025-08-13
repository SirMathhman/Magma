const { compile } = require('./compile');

describe('compile Magma to C', () => {
  test('compiles empty struct', () => {
    expect(compile('struct Empty {}')).toBe('struct Empty {};');
  });

  test('function returns correct value', () => {
    const src = `fn getVal(): I32 => { return 42; } let x : I32 = getVal();`;
    const expected = 'int32_t getVal() {return 42;} int32_t x = getVal();';
    expect(compile(src)).toBe(expected);
  });

  test('function call with argument returns correct value', () => {
    const src = `fn add(a : I32, b : I32): I32 => { return a + b; } let x : I32 = add(10, 32);`;
    const expected = 'int32_t add(int32_t a, int32_t b) {return a + b;} int32_t x = add(10, 32);';
    expect(compile(src)).toBe(expected);
  });

  test('nested function calls', () => {
    const src = `fn double(x : I32): I32 => { return x * 2; } fn triple(x : I32): I32 => { return x * 3; } let y : I32 = double(triple(5));`;
    const expected = 'int32_t double(int32_t x) {return x * 2;} int32_t triple(int32_t x) {return x * 3;} int32_t y = double(triple(5));';
    expect(compile(src)).toBe(expected);
  });

  test('multiple function calls', () => {
    const src = `fn inc(x : I32): I32 => { return x + 1; } let a : I32 = inc(1); let b : I32 = inc(2);`;
    const expected = 'int32_t inc(int32_t x) {return x + 1;} int32_t a = inc(1); int32_t b = inc(2);';
    expect(compile(src)).toBe(expected);
  });

    test('compiles struct instantiation and field access', () => {
      const src = `struct Wrapper { x : I32 } let created = Wrapper {10}; let inner = created.x;`;
      const expected = 'struct Wrapper { int32_t x; }; struct Wrapper created = { 10 }; int32_t inner = created.x;';
      expect(compile(src)).toBe(expected);
    });
  test('compiles function call with return value assigned to variable', () => {
    expect(compile('fn get() : I32 => {return 100;} let value : I32 = get();')).toBe('int32_t get() {return 100;} int32_t value = get();');
  });
  test('compiles function declaration followed by function call', () => {
    expect(compile('fn empty() : Void => {} empty();')).toBe('void empty() {} empty();');
  });

  test('compiles function declaration with non-Void return type and return statement', () => {
    expect(compile('fn get(): I32 => {return 100;}')).toBe('int32_t get() {return 100;}');
  });
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
  test('compiles struct with one field', () => {
    expect(compile('struct Wrapper { value : I32 }')).toBe('struct Wrapper { int32_t value; };');
  });

  test('compiles struct with multiple fields', () => {
    expect(compile('struct Point { x : I32; y : I32 }')).toBe('struct Point { int32_t x; int32_t y; };');
  });

  test('compiles struct construction', () => {
    expect(compile('struct Point { x : I32, y : I32 } let myPoint : Point = Point { 3, 4 };')).toBe('struct Point { int32_t x; int32_t y; }; struct Point myPoint = { 3, 4 };');
  });

  // Comprehensive test cases - not yet implemented
  
  // Advanced struct tests
  test('compiles nested struct field access', () => {
    const src = `struct Point { x : I32, y : I32 } struct Line { start : Point, end : Point } let line = Line { Point {0, 0}, Point {10, 10} }; let x = line.start.x;`;
    const expected = 'struct Point { int32_t x; int32_t y; }; struct Line { struct Point start; struct Point end; }; struct Line line = { { 0, 0 }, { 10, 10 } }; int32_t x = line.start.x;';
    expect(compile(src)).toBe(expected);
  });

  test('compiles struct with mixed field types', () => {
    const src = `struct Person { age : I32, height : U16, name : [U8; 32], active : Bool } let person = Person {25, 180, "John", true};`;
    const expected = 'struct Person { int32_t age; uint16_t height; uint8_t name[32]; bool active; }; struct Person person = { 25, 180, "John", true };';
    expect(compile(src)).toBe(expected);
  });

  test('compiles struct field modification', () => {
    const src = `struct Point { x : I32, y : I32 } let mut p = Point {1, 2}; p.x = 5;`;
    const expected = 'struct Point { int32_t x; int32_t y; }; struct Point p = { 1, 2 }; p.x = 5;';
    expect(compile(src)).toBe(expected);
  });

  // Advanced function tests
  test('compiles recursive function', () => {
    const src = `fn factorial(n : I32): I32 => { if (n <= 1) { return 1; } else { return n * factorial(n - 1); } }`;
    const expected = 'int32_t factorial(int32_t n) {if(n <= 1){return 1;}else{return n * factorial(n - 1);}}';
    expect(compile(src)).toBe(expected);
  });

  test('compiles function with struct parameters', () => {
    const src = `struct Point { x : I32, y : I32 } fn distance(p1 : Point, p2 : Point): I32 => { return (p1.x - p2.x) + (p1.y - p2.y); }`;
    const expected = 'struct Point { int32_t x; int32_t y; }; int32_t distance(struct Point p1, struct Point p2) {return (p1.x - p2.x) + (p1.y - p2.y);}';
    expect(compile(src)).toBe(expected);
  });

  test('compiles function returning struct', () => {
    const src = `struct Point { x : I32, y : I32 } fn createPoint(x : I32, y : I32): Point => { return Point {x, y}; }`;
    const expected = 'struct Point { int32_t x; int32_t y; }; struct Point createPoint(int32_t x, int32_t y) {return (struct Point){ x, y };}';
    expect(compile(src)).toBe(expected);
  });

  test('compiles higher-order function concepts', () => {
    const src = `fn apply(x : I32, f : I32): I32 => { return f + x; } fn double(x : I32): I32 => { return x * 2; } let result = apply(5, double(3));`;
    const expected = 'int32_t apply(int32_t x, int32_t f) {return f + x;} int32_t double(int32_t x) {return x * 2;} int32_t result = apply(5, double(3));';
    expect(compile(src)).toBe(expected);
  });

  // Advanced control flow tests
  test('compiles complex nested if-else chains', () => {
    const src = `fn grade(score : I32): I32 => { if (score >= 90) { return 4; } else if (score >= 80) { return 3; } else if (score >= 70) { return 2; } else if (score >= 60) { return 1; } else { return 0; } }`;
    const expected = 'int32_t grade(int32_t score) {if(score >= 90){return 4;}else if(score >= 80){return 3;}else if(score >= 70){return 2;}else if(score >= 60){return 1;}else{return 0;}}';
    expect(compile(src)).toBe(expected);
  });

  test('compiles for loop simulation with while', () => {
    const src = `fn sum(n : I32): I32 => { let mut total = 0; let mut i = 1; while (i <= n) { total = total + i; i = i + 1; } return total; }`;
    const expected = 'int32_t sum(int32_t n) {int32_t total = 0; int32_t i = 1; while(i <= n){total = total + i; i = i + 1;} return total;}';
    expect(compile(src)).toBe(expected);
  });

  test('compiles break and continue in while loops', () => {
    const src = `fn findFirst(arr : [I32; 10], target : I32): I32 => { let mut i = 0; while (i < 10) { if (arr[i] == target) { break; } i = i + 1; } return i; }`;
    const expected = 'int32_t findFirst(int32_t arr[10], int32_t target) {int32_t i = 0; while(i < 10){if(arr[i] == target){break;} i = i + 1;} return i;}';
    expect(compile(src)).toBe(expected);
  });

  // Advanced array and memory tests
  test('compiles multi-dimensional arrays', () => {
    const src = `let matrix : [[I32; 3]; 3] = [[1, 2, 3], [4, 5, 6], [7, 8, 9]]; let val = matrix[1][2];`;
    const expected = 'int32_t matrix[3][3] = {{1, 2, 3}, {4, 5, 6}, {7, 8, 9}}; int32_t val = matrix[1][2];';
    expect(compile(src)).toBe(expected);
  });

  test('compiles array of structs', () => {
    const src = `struct Point { x : I32, y : I32 } let points : [Point; 3] = [Point {0, 0}, Point {1, 1}, Point {2, 2}];`;
    const expected = 'struct Point { int32_t x; int32_t y; }; struct Point points[3] = {{0, 0}, {1, 1}, {2, 2}};';
    expect(compile(src)).toBe(expected);
  });

  test('compiles dynamic array operations', () => {
    const src = `fn arraySum(arr : [I32; 10], len : I32): I32 => { let mut sum = 0; let mut i = 0; while (i < len) { sum = sum + arr[i]; i = i + 1; } return sum; }`;
    const expected = 'int32_t arraySum(int32_t arr[10], int32_t len) {int32_t sum = 0; int32_t i = 0; while(i < len){sum = sum + arr[i]; i = i + 1;} return sum;}';
    expect(compile(src)).toBe(expected);
  });

  // Advanced type system tests
  test('compiles type aliases and custom types', () => {
    const src = `type UserId = I32; type UserName = [U8; 64]; struct User { id : UserId, name : UserName, active : Bool }`;
    const expected = 'typedef int32_t UserId; typedef uint8_t UserName[64]; struct User { UserId id; UserName name; bool active; };';
    expect(compile(src)).toBe(expected);
  });

  test('compiles generic struct concepts', () => {
    const src = `struct Box<T> { value : T } let intBox = Box<I32> {42}; let strBox = Box<[U8; 10]> {"hello"};`;
    const expected = 'struct Box_I32 { int32_t value; }; struct Box_U8_array_10 { uint8_t value[10]; }; struct Box_I32 intBox = {42}; struct Box_U8_array_10 strBox = {"hello"};';
    expect(compile(src)).toBe(expected);
  });

  test('compiles enum-like structures', () => {
    const src = `struct Color { r : U8, g : U8, b : U8 } let red = Color {255, 0, 0}; let green = Color {0, 255, 0}; let blue = Color {0, 0, 255};`;
    const expected = 'struct Color { uint8_t r; uint8_t g; uint8_t b; }; struct Color red = { 255, 0, 0 }; struct Color green = { 0, 255, 0 }; struct Color blue = { 0, 0, 255 };';
    expect(compile(src)).toBe(expected);
  });

  // Advanced string and character tests
  test('compiles string manipulation functions', () => {
    const src = `fn strlen(str : [U8; 256]): I32 => { let mut len = 0; while (str[len] != 0) { len = len + 1; } return len; }`;
    const expected = 'int32_t strlen(uint8_t str[256]) {int32_t len = 0; while(str[len] != 0){len = len + 1;} return len;}';
    expect(compile(src)).toBe(expected);
  });

  test('compiles character array operations', () => {
    const src = `let mut buffer : [U8; 100] = [0; 100]; buffer[0] = 'H'; buffer[1] = 'i'; buffer[2] = 0;`;
    const expected = 'uint8_t buffer[100] = {0}; buffer[0] = \'H\'; buffer[1] = \'i\'; buffer[2] = 0;';
    expect(compile(src)).toBe(expected);
  });

  test('compiles string comparison', () => {
    const src = `fn strcmp(s1 : [U8; 100], s2 : [U8; 100]): Bool => { let mut i = 0; while (s1[i] != 0 && s2[i] != 0) { if (s1[i] != s2[i]) { return false; } i = i + 1; } return s1[i] == s2[i]; }`;
    const expected = 'bool strcmp(uint8_t s1[100], uint8_t s2[100]) {int32_t i = 0; while(s1[i] != 0 && s2[i] != 0){if(s1[i] != s2[i]){return false;} i = i + 1;} return s1[i] == s2[i];}';
    expect(compile(src)).toBe(expected);
  });

  // Advanced error handling and edge cases
  test('compiles complex expressions with precedence', () => {
    const src = `let result = 2 + 3 * 4 - 1 / 2 + (5 - 3) * 2;`;
    const expected = 'int32_t result = 2 + 3 * 4 - 1 / 2 + (5 - 3) * 2;';
    expect(compile(src)).toBe(expected);
  });

  test('compiles deeply nested function calls', () => {
    const src = `fn f(x : I32): I32 => { return x + 1; } fn g(x : I32): I32 => { return x * 2; } fn h(x : I32): I32 => { return x - 1; } let result = f(g(h(f(g(5)))));`;
    const expected = 'int32_t f(int32_t x) {return x + 1;} int32_t g(int32_t x) {return x * 2;} int32_t h(int32_t x) {return x - 1;} int32_t result = f(g(h(f(g(5)))));';
    expect(compile(src)).toBe(expected);
  });

  test('compiles mixed type operations with casting', () => {
    const src = `let a : I32 = 10; let b : U8 = 5; let result : I32 = a + (I32)b;`;
    const expected = 'int32_t a = 10; uint8_t b = 5; int32_t result = a + (int32_t)b;';
    expect(compile(src)).toBe(expected);
  });

  // Memory and pointer-like operations
  test('compiles reference-like operations', () => {
    const src = `struct Node { value : I32, next : Node } let mut node1 = Node {1, Node {2, Node {3, Node {0, Node {0, Node {0, Node {0, Node {0, Node {0, Node {0, Node {0, Node {0}}}}}}}}}}};`;
    const expected = 'struct Node { int32_t value; struct Node next; }; struct Node node1 = {1, {2, {3, {0, {0, {0, {0, {0, {0, {0, {0, {0}}}}}}}}}}}};';
    expect(compile(src)).toBe(expected);
  });

  test('compiles array passing to functions', () => {
    const src = `fn processArray(arr : [I32; 10], callback : I32): [I32; 10] => { let mut result : [I32; 10] = [0; 10]; let mut i = 0; while (i < 10) { result[i] = arr[i] + callback; i = i + 1; } return result; }`;
    const expected = 'int32_t processArray[10](int32_t arr[10], int32_t callback) {int32_t result[10] = {0}; int32_t i = 0; while(i < 10){result[i] = arr[i] + callback; i = i + 1;} return result;}';
    expect(compile(src)).toBe(expected);
  });

  // Advanced compilation edge cases
  test('compiles empty function with all return paths', () => {
    const src = `fn conditional(flag : Bool): I32 => { if (flag) { if (true) { return 1; } else { return 2; } } else { return 3; } }`;
    const expected = 'int32_t conditional(bool flag) {if(flag){if(true){return 1;}else{return 2;}}else{return 3;}}';
    expect(compile(src)).toBe(expected);
  });

  test('compiles variable shadowing in different scopes', () => {
    const src = `let x = 1; { let x = 2; { let x = 3; } } let y = x;`;
    const expected = 'int32_t x = 1; {int32_t x = 2; {int32_t x = 3;}} int32_t y = x;';
    expect(compile(src)).toBe(expected);
  });

  test('compiles complex boolean expressions', () => {
    const src = `let result = (true && false) || (true && !false) || (!true && false);`;
    const expected = 'bool result = (true && false) || (true && !false) || (!true && false);';
    expect(compile(src)).toBe(expected);
  });
});