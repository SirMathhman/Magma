import compile from '../src/compile';

test('returns empty string when given empty input', () => {
  expect(compile('')).toBe('');
});

test('throws when given non-empty input', () => {
  expect(() => compile('hello')).toThrow('This function always throws');
});

test('transforms `let` declaration to `int32_t` and adds stdint header', () => {
  expect(compile('let x = 10;')).toBe('#include <stdint.h>\r\nint32_t x = 10;');
});

test('transforms typed I32 `let` declaration and normalizes spacing', () => {
  expect(compile('let x : I32 =  0;')).toBe('#include <stdint.h>\r\nint32_t x = 0;');
});

test('strips I32 suffix from numeric literal values', () => {
  expect(compile('let x = 0I32;')).toBe('#include <stdint.h>\r\nint32_t x = 0;');
});

test('handles typed annotation and I32 literal suffix together', () => {
  expect(compile('let x : I32 = 0I32;')).toBe('#include <stdint.h>\r\nint32_t x = 0;');
});

const signedTypes = ['I8', 'I16', 'I32', 'I64'];
for (const t of signedTypes) {
  test(`maps ${t} to corresponding int type`, () => {
    const bits = t.slice(1);
    expect(compile(`let a : ${t} = 1;`)).toBe(`#include <stdint.h>\r\nint${bits}_t a = 1;`);
  });
}

const unsignedTypes = ['U8', 'U16', 'U32', 'U64'];
for (const t of unsignedTypes) {
  test(`maps ${t} to corresponding uint type`, () => {
    const bits = t.slice(1);
    expect(compile(`let b : ${t} = 2;`)).toBe(`#include <stdint.h>\r\nuint${bits}_t b = 2;`);
  });
}

for (const t of signedTypes) {
  test(`maps literal-suffix ${t} to corresponding int type`, () => {
    const bits = t.slice(1);
    expect(compile(`let a = 1${t};`)).toBe(`#include <stdint.h>\r\nint${bits}_t a = 1;`);
  });
}

for (const t of unsignedTypes) {
  test(`maps literal-suffix ${t} to corresponding uint type`, () => {
    const bits = t.slice(1);
    expect(compile(`let b = 2${t};`)).toBe(`#include <stdint.h>\r\nuint${bits}_t b = 2;`);
  });
}

for (const t of signedTypes) {
  test(`maps annotated+suffix ${t} to corresponding int type`, () => {
    const bits = t.slice(1);
    expect(compile(`let a : ${t} = 1${t};`)).toBe(`#include <stdint.h>\r\nint${bits}_t a = 1;`);
  });
}

for (const t of unsignedTypes) {
  test(`maps annotated+suffix ${t} to corresponding uint type`, () => {
    const bits = t.slice(1);
    expect(compile(`let b : ${t} = 2${t};`)).toBe(`#include <stdint.h>\r\nuint${bits}_t b = 2;`);
  });
}

test('throws when annotation and literal suffix mismatch', () => {
  expect(() => compile('let x : I32 = 0U64;')).toThrow();
});

test('transforms boolean literal to stdbool bool', () => {
  expect(compile('let test = true;')).toBe('#include <stdbool.h>\r\nbool test = true;');
});

test('transforms boolean false literal to stdbool bool', () => {
  expect(compile('let test = false;')).toBe('#include <stdbool.h>\r\nbool test = false;');
});

test('transforms Bool annotation with true literal to stdbool', () => {
  expect(compile('let test : Bool = true;')).toBe('#include <stdbool.h>\r\nbool test = true;');
});

test('transforms Bool annotation with false literal to stdbool', () => {
  expect(compile('let test : Bool = false;')).toBe('#include <stdbool.h>\r\nbool test = false;');
});

test('throws when Bool annotation is given a numeric literal', () => {
  expect(() => compile('let x : Bool = 0I32;')).toThrow();
});

test('throws when numeric annotation is given a boolean literal', () => {
  expect(() => compile('let x : I32 = true;')).toThrow();
});

test('maps F32 annotation to float', () => {
  expect(compile('let x : F32 = 0.0;')).toBe('float x = 0.0;');
});

test('maps F64 annotation to double', () => {
  expect(compile('let x : F64 = 0.0;')).toBe('double x = 0.0;');
});

test('allows integer literal for F32 annotation', () => {
  expect(compile('let x : F32 = 5;')).toBe('float x = 5;');
});

test('unannotated float literal defaults to float', () => {
  expect(compile('let x = 5.0;')).toBe('float x = 5.0;');
});

test('throws when float annotation is paired with integer-suffixed literal', () => {
  expect(() => compile('let x : F32 = 0I32;')).toThrow();
});

test('throws when float annotation is given a boolean literal', () => {
  expect(() => compile('let x : F32 = true;')).toThrow();
});

test('accepts float literal suffix F32', () => {
  expect(compile('let x = 0.0F32;')).toBe('float x = 0.0;');
});

test('accepts float literal suffix F64', () => {
  expect(compile('let x = 0.0F64;')).toBe('double x = 0.0;');
});

test('handles reference between declarations (let x = 10; let y = x;)', () => {
  expect(compile('let x = 10; let y = x;')).toBe('#include <stdint.h>' + '\r\n' + 'int32_t x = 10;' + '\r\n' + 'int32_t y = x;');
});

test('handles mutable declaration and subsequent assignment', () => {
  expect(compile('let mut x = 10; x = 100;')).toBe('#include <stdint.h>' + '\r\n' + 'int32_t x = 10;' + '\r\n' + 'x = 100;');
});

test('throws when assigning to non-mutable variable', () => {
  expect(() => compile('let x = 10; x = 100;')).toThrow();
});

test('throws when assigning boolean to integer variable', () => {
  expect(() => compile('let mut x = 10; x = true;')).toThrow();
});

test('throws when assigning float to non-mutable integer variable', () => {
  expect(() => compile('let x = 10; x = 0.0;')).toThrow();
});

test('char literal without annotation becomes U8', () => {
  expect(compile("let x = 'a';")).toBe('#include <stdint.h>' + '\r\n' + 'uint8_t x = \'a\';');
});

test('typed integer annotation with char literal should throw', () => {
  expect(() => compile("let x : U32 = 'a';")).toThrow();
});

test('array annotation [U8; 3] becomes uint8_t array', () => {
  expect(compile('let x : [U8; 3] = [1, 2, 3];')).toBe('#include <stdint.h>' + '\r\n' + 'uint8_t x[3] = {1, 2, 3};');
});

test('unannotated array literal becomes int32_t array', () => {
  expect(compile('let x = [1, 2, 3];')).toBe('#include <stdint.h>' + '\r\n' + 'int32_t x[3] = {1, 2, 3};');
});

test('unannotated boolean array literal becomes bool array', () => {
  expect(compile('let x = [true, false, true];')).toBe('#include <stdbool.h>' + '\r\n' + 'bool x[3] = {true, false, true};');
});

test('throws when array literal contains mixed types', () => {
  expect(() => compile('let x = [true, 0.0, 5];')).toThrow();
});

test('throws when array literal is empty', () => {
  expect(() => compile('let x = [];')).toThrow();
});

test('typed empty Bool array becomes bool x[0] = {}', () => {
  expect(compile('let x : [Bool; 0] = [];')).toBe('#include <stdbool.h>' + '\r\n' + 'bool x[0] = {};');
});

test('throws when typed Bool array is initialized with numeric literals', () => {
  expect(() => compile('let x : [Bool; 3] = [1, 2, 3];')).toThrow();
});

test('throws when typed Bool array initializer length mismatches declared length', () => {
  expect(() => compile('let x : [Bool; 3] = [1, 2];')).toThrow();
});

test('typed F32 array with integer initializers becomes float array', () => {
  expect(compile('let x : [F32; 3] = [1, 2, 3];')).toBe('float x[3] = {1, 2, 3};');
});

test('typed F32 array accepts mixed int and float initializers', () => {
  expect(compile('let x : [F32; 3] = [1, 2.0, 3];')).toBe('float x[3] = {1, 2.0, 3};');
});

test('bare braces `{}` should be passed through unchanged', () => {
  expect(compile('{}')).toBe('{}');
});

test('double braces `{{}}` should be passed through unchanged', () => {
  expect(compile('{{}}')).toBe('{\r\n\t{}\r\n}');
});

test('nested empty braces `{{}{}}` should be passed through unchanged', () => {
  expect(compile('{{}{}}')).toBe('{\r\n\t{}\r\n\t{}\r\n}');
});

test('braced let block {let x = 0;} becomes include + braced decl', () => {
  expect(compile('{let x = 0;}')).toBe('#include <stdint.h>' + '\r\n' + '{\r\n\tint32_t x = 0;\r\n}');
});

test('double-braced let block {{let x = 0;}} becomes include + double-braced decl', () => {
  expect(compile('{{let x = 0;}}')).toBe('#include <stdint.h>' + '\r\n' + '{\r\n\t{\r\n\t\tint32_t x = 0;\r\n\t}\r\n}');
});

test('let x = 0; {} becomes include + decl + {} on next line', () => {
  expect(compile('let x = 0; {}')).toBe('#include <stdint.h>' + '\r\n' + 'int32_t x = 0;' + '\r\n' + '{}');
});

test('{} let x = 0; becomes include + {} then decl', () => {
  expect(compile('{} let x = 0;')).toBe('#include <stdint.h>' + '\r\n' + '{}' + '\r\n' + 'int32_t x = 0;');
});

test('braced block with two lets becomes include + one-line braced decls', () => {
  expect(compile('{let x = 0; let y = x;}')).toBe('#include <stdint.h>' + '\r\n' + '{\r\n\tint32_t x = 0;\r\n\tint32_t y = x;\r\n}');
});

test('let then braced let referencing it is valid', () => {
  expect(compile('let x = 0; {let y = x;}')).toBe('#include <stdint.h>' + '\r\n' + 'int32_t x = 0;' + '\r\n' + '{\r\n\tint32_t y = x;\r\n}');
});

test('braced let then let referencing it is invalid', () => {
  expect(() => compile('{let x = 0;} let y = x;')).toThrow();
});

test('compiles `fn test() : Void => {}` to `void test(){}`', () => {
  expect(compile('fn test() : Void => {}')).toBe('void test(){}');
});

test('compiles `fn test() : Bool => {return true;}` to bool function with stdbool include', () => {
  const out = compile('fn test() : Bool => {return true;}');
  expect(out).toBe('#include <stdbool.h>\r\nbool test(){\r\n\treturn true;\r\n}');
});

test('compiles `fn test() => {}` to `void test(){}` (no return annotation)', () => {
  expect(compile('fn test() => {}')).toBe('void test(){}');
});

test('infers bool for `fn test() => {return true;}` and emits stdbool include', () => {
  const out = compile('fn test() => {return true;}');
  expect(out).toBe('#include <stdbool.h>\r\nbool test(){\r\n\treturn true;\r\n}');
});

test('unannotated comparison `let x = 1 < 2;` becomes bool with stdbool include', () => {
  expect(compile('let x = 1 < 2;')).toBe('#include <stdbool.h>\r\nbool x = 1 < 2;');
});

test('unannotated float comparison `let x = 1.0F32 < 2.0F32;` becomes bool', () => {
  expect(compile('let x = 1.0F32 < 2.0F32;')).toBe('#include <stdbool.h>\r\nbool x = 1.0 < 2.0;');
});

test('mixed comparison with non-numeric should throw', () => {
  expect(() => compile("let x = 'a' < 2; ")).toThrow();
});

test('passthrough top-level if statement', () => {
  expect(compile('if(true){}')).toBe('if(true){}');
});

test('if without braces with inner let declaration becomes braced C block with include', () => {
  expect(compile('if(true) let x = 0;')).toBe('#include <stdint.h>' + '\r\n' + 'if(true){' + '\r\n\tint32_t x = 0;\r\n}');
});

test('throws when if condition is non-boolean like `if(5){}`', () => {
  expect(() => compile('if(5){}')).toThrow();
});

test('throws when logical OR has non-boolean operand `let x = 5 || false;`', () => {
  expect(() => compile('let x = 5 || false;')).toThrow();
});

test('string literal becomes const char* for immutable let', () => {
  expect(compile('let x = "hello";')).toBe('const char* x = "hello";');
});

test('string equality compiles to strcmp and includes string.h', () => {
  expect(compile('let x = "first" == "second";')).toBe('#include <stdbool.h>\r\n#include <string.h>\r\nbool x = strcmp("first", "second") != 0;');
});

test('fn parameter annotated String becomes char* parameter', () => {
  expect(compile('fn greet(s : *CStr) => {}')).toBe('void greet(char* s){}');
});

test('fn isBoolLiteral with CStr parameter and string comparisons compiles with strcmp and preserves precedence', () => {
  const src = 'fn isBoolLiteral(v : *CStr) => {return v == "true" || v == "false";}';
  const out = compile(src);
  // expect strcmp transforms for each == and OR remains
  expect(out).toBe('#include <stdbool.h>\r\n#include <string.h>\r\nbool isBoolLiteral(char* v){\r\n\treturn strcmp(v, "true") != 0 || strcmp(v, "false") != 0;\r\n}');
});

test('two consecutive empty functions compile to two void functions on separate lines', () => {
  expect(compile('fn first() => {} fn second() => {}')).toBe('void first(){}\r\nvoid second(){}');
});

test('post-increment on mutable int variable compiles', () => {
  expect(compile('let mut x = 0; x++;')).toBe('#include <stdint.h>\r\nint32_t x = 0;\r\nx++;');
});

test('for loop with let initializer compiles with typed initializer', () => {
  expect(compile('for(let mut x = 0; x < 10; x++){}')).toBe('#include <stdint.h>\r\nfor(int32_t x = 0; x < 10; x++){}');
});

test('single-line comment on its own is ignored', () => {
  expect(compile('// this is a comment')).toBe('');
});

test('single-line trailing comment is ignored after content', () => {
  expect(compile('let x = 0; // trailing comment')).toBe('#include <stdint.h>\r\nint32_t x = 0;');
});

test('if condition accepts string length comparison test.length == 0', () => {
  const src = 'let test = ""; if(test.length == 0){}';
  const out = compile(src);
  expect(out).toBe('#include <string.h>\r\nconst char* test = "";\r\nif(strlen(test) == 0){}');
});

test('array indexing x[0] returns element and compiles', () => {
  const src = 'let x = [1, 2, 3]; let y = x[0];';
  const out = compile(src);
  expect(out).toBe('#include <stdint.h>\r\nint32_t x[3] = {1, 2, 3};\r\nint32_t y = x[0];');
});

// Additional positive tests for features we probably should support but haven't explicitly verified yet.
test('if-else passthrough with braced blocks', () => {
  expect(compile('if(true){} else {}')).toBe('if(true){} else {}');
});

test('else-if chain passthrough', () => {
  expect(compile('if(true){} else if(false){} else {}')).toBe('if(true){} else if(false){} else {}');
});

test('function with two int params and inferred bool return from comparison', () => {
  const src = 'fn cmp(a : I32, b : I32) => {return a < b;}';
  const out = compile(src);
  expect(out).toBe('#include <stdbool.h>\r\nbool cmp(int32_t a, int32_t b){\r\n\treturn a < b;\r\n}');
});

test('explicit typed function return I32 should map to int32_t', () => {
  expect(compile('fn add() : I32 => {return 1I32;}')).toBe('#include <stdint.h>\r\nint32_t add(){\r\n\treturn 1;\r\n}');
});

test('while loop passthrough', () => {
  expect(compile('while(true){}')).toBe('while(true){}');
});

test('array element assignment compiles', () => {
  const src = 'let mut a = [1, 2, 3]; a[1] = 5;';
  expect(compile(src)).toBe('#include <stdint.h>\r\nint32_t a[3] = {1, 2, 3};\r\na[1] = 5;');
});

test('function with explicit F64 return maps to double', () => {
  expect(compile('fn pi() : F64 => {return 3.0F64;}')).toBe('double pi(){\r\n\treturn 3.0;\r\n}');
});

// New tests for likely-error scenarios (these are intentionally not implemented in the compiler yet)
test('if without braces with non-boolean condition should throw', () => {
  expect(() => compile('if(5) let x = 0;')).toThrow();
});

test('if single-statement inner let should not leak to outer scope (use after) and should throw when referenced', () => {
  expect(() => compile('if(true) let x = 0; let y = x;')).toThrow();
});

test('function annotated Bool returning numeric literal should throw', () => {
  expect(() => compile('fn bad() : Bool => {return 1;}')).toThrow();
});

test('for loop with non-boolean condition should throw', () => {
  expect(() => compile('for(; 5; ) {}')).toThrow();
});

test('for-let initializer with mismatched annotation and literal suffix should throw', () => {
  expect(() => compile('for(let x : I32 = 0U32; x < 10; x++){}')).toThrow();
});

test('typed integer array initialized with mixed types should throw', () => {
  expect(() => compile('let x : [I32; 3] = [1, true, 3];')).toThrow();
});

test('if condition referencing undeclared variable should throw', () => {
  expect(() => compile('if(x){}')).toThrow();
});

test('post-increment on immutable variable should throw', () => {
  expect(() => compile('let x = 0; x++;')).toThrow();
});

test('typed array with declared non-zero length and empty initializer should throw', () => {
  expect(() => compile('let x : [I32; 2] = [];')).toThrow();
});

test('float annotation initialized with char literal should throw', () => {
  expect(() => compile("let x : F32 = 'a';")).toThrow();
});

test('using undeclared variable in expression should throw', () => {
  expect(() => compile('let y = z + 1;')).toThrow();
});

test('???', () => {
  expect(() => compile(`fn isFloatLiteral(v : *CStr) => {
  if (strlen(v) == 0) return false;
  let mut dotIndex = -1;
  for (let mut i = 0; i < strlen(v); i = i + 1) {
    let ch = v[i];
    if (ch == '.') {
      if (dotIndex != -1) return false;
      dotIndex = i;
      continue;
    }
    if (ch < '0' || ch > '9') return false;
  }
  return dotIndex != -1 && dotIndex != strlen(v) - 1;
}`)).toBe("???");
});
