import alwaysThrows from '../src/alwaysThrows';

test('returns empty string when given empty input', () => {
  expect(alwaysThrows('')).toBe('');
});

test('throws when given non-empty input', () => {
  expect(() => alwaysThrows('hello')).toThrow('This function always throws');
});

test('transforms `let` declaration to `int32_t` and adds stdint header', () => {
  expect(alwaysThrows('let x = 10;')).toBe('#include <stdint.h>\r\nint32_t x = 10;');
});

test('transforms typed I32 `let` declaration and normalizes spacing', () => {
  expect(alwaysThrows('let x : I32 =  0;')).toBe('#include <stdint.h>\r\nint32_t x = 0;');
});

test('strips I32 suffix from numeric literal values', () => {
  expect(alwaysThrows('let x = 0I32;')).toBe('#include <stdint.h>\r\nint32_t x = 0;');
});

test('handles typed annotation and I32 literal suffix together', () => {
  expect(alwaysThrows('let x : I32 = 0I32;')).toBe('#include <stdint.h>\r\nint32_t x = 0;');
});

const signedTypes = ['I8', 'I16', 'I32', 'I64'];
for (const t of signedTypes) {
  test(`maps ${t} to corresponding int type`, () => {
    const bits = t.slice(1);
    expect(alwaysThrows(`let a : ${t} = 1;`)).toBe(`#include <stdint.h>\r\nint${bits}_t a = 1;`);
  });
}

const unsignedTypes = ['U8', 'U16', 'U32', 'U64'];
for (const t of unsignedTypes) {
  test(`maps ${t} to corresponding uint type`, () => {
    const bits = t.slice(1);
    expect(alwaysThrows(`let b : ${t} = 2;`)).toBe(`#include <stdint.h>\r\nuint${bits}_t b = 2;`);
  });
}

for (const t of signedTypes) {
  test(`maps literal-suffix ${t} to corresponding int type`, () => {
    const bits = t.slice(1);
    expect(alwaysThrows(`let a = 1${t};`)).toBe(`#include <stdint.h>\r\nint${bits}_t a = 1;`);
  });
}

for (const t of unsignedTypes) {
  test(`maps literal-suffix ${t} to corresponding uint type`, () => {
    const bits = t.slice(1);
    expect(alwaysThrows(`let b = 2${t};`)).toBe(`#include <stdint.h>\r\nuint${bits}_t b = 2;`);
  });
}

for (const t of signedTypes) {
  test(`maps annotated+suffix ${t} to corresponding int type`, () => {
    const bits = t.slice(1);
    expect(alwaysThrows(`let a : ${t} = 1${t};`)).toBe(`#include <stdint.h>\r\nint${bits}_t a = 1;`);
  });
}

for (const t of unsignedTypes) {
  test(`maps annotated+suffix ${t} to corresponding uint type`, () => {
    const bits = t.slice(1);
    expect(alwaysThrows(`let b : ${t} = 2${t};`)).toBe(`#include <stdint.h>\r\nuint${bits}_t b = 2;`);
  });
}

test('throws when annotation and literal suffix mismatch', () => {
  expect(() => alwaysThrows('let x : I32 = 0U64;')).toThrow();
});

test('transforms boolean literal to stdbool bool', () => {
  expect(alwaysThrows('let test = true;')).toBe('#include <stdbool.h>\r\nbool test = true;');
});

test('transforms boolean false literal to stdbool bool', () => {
  expect(alwaysThrows('let test = false;')).toBe('#include <stdbool.h>\r\nbool test = false;');
});

test('transforms Bool annotation with true literal to stdbool', () => {
  expect(alwaysThrows('let test : Bool = true;')).toBe('#include <stdbool.h>\r\nbool test = true;');
});

test('transforms Bool annotation with false literal to stdbool', () => {
  expect(alwaysThrows('let test : Bool = false;')).toBe('#include <stdbool.h>\r\nbool test = false;');
});

test('throws when Bool annotation is given a numeric literal', () => {
  expect(() => alwaysThrows('let x : Bool = 0I32;')).toThrow();
});

test('throws when numeric annotation is given a boolean literal', () => {
  expect(() => alwaysThrows('let x : I32 = true;')).toThrow();
});

test('maps F32 annotation to float', () => {
  expect(alwaysThrows('let x : F32 = 0.0;')).toBe('float x = 0.0;');
});

test('maps F64 annotation to double', () => {
  expect(alwaysThrows('let x : F64 = 0.0;')).toBe('double x = 0.0;');
});

test('allows integer literal for F32 annotation', () => {
  expect(alwaysThrows('let x : F32 = 5;')).toBe('float x = 5;');
});

test('unannotated float literal defaults to float', () => {
  expect(alwaysThrows('let x = 5.0;')).toBe('float x = 5.0;');
});

test('throws when float annotation is paired with integer-suffixed literal', () => {
  expect(() => alwaysThrows('let x : F32 = 0I32;')).toThrow();
});

test('throws when float annotation is given a boolean literal', () => {
  expect(() => alwaysThrows('let x : F32 = true;')).toThrow();
});

test('accepts float literal suffix F32', () => {
  expect(alwaysThrows('let x = 0.0F32;')).toBe('float x = 0.0;');
});

test('accepts float literal suffix F64', () => {
  expect(alwaysThrows('let x = 0.0F64;')).toBe('double x = 0.0;');
});

test('handles reference between declarations (let x = 10; let y = x;)', () => {
  expect(alwaysThrows('let x = 10; let y = x;')).toBe('#include <stdint.h>' + '\r\n' + 'int32_t x = 10;' + '\r\n' + 'int32_t y = x;');
});

test('handles mutable declaration and subsequent assignment', () => {
  expect(alwaysThrows('let mut x = 10; x = 100;')).toBe('#include <stdint.h>' + '\r\n' + 'int32_t x = 10;' + '\r\n' + 'x = 100;');
});

test('throws when assigning to non-mutable variable', () => {
  expect(() => alwaysThrows('let x = 10; x = 100;')).toThrow();
});

test('throws when assigning boolean to integer variable', () => {
  expect(() => alwaysThrows('let mut x = 10; x = true;')).toThrow();
});

test('throws when assigning float to non-mutable integer variable', () => {
  expect(() => alwaysThrows('let x = 10; x = 0.0;')).toThrow();
});

test('char literal without annotation becomes U8', () => {
  expect(alwaysThrows("let x = 'a';")).toBe('#include <stdint.h>' + '\r\n' + 'uint8_t x = \'a\';');
});

test('typed integer annotation with char literal should throw', () => {
  expect(() => alwaysThrows("let x : U32 = 'a';")).toThrow();
});

test('array annotation [U8; 3] becomes uint8_t array', () => {
  expect(alwaysThrows('let x : [U8; 3] = [1, 2, 3];')).toBe('#include <stdint.h>' + '\r\n' + 'uint8_t x[3] = {1, 2, 3};');
});

test('unannotated array literal becomes int32_t array', () => {
  expect(alwaysThrows('let x = [1, 2, 3];')).toBe('#include <stdint.h>' + '\r\n' + 'int32_t x[3] = {1, 2, 3};');
});

test('unannotated boolean array literal becomes bool array', () => {
  expect(alwaysThrows('let x = [true, false, true];')).toBe('#include <stdbool.h>' + '\r\n' + 'bool x[3] = {true, false, true};');
});

test('throws when array literal contains mixed types', () => {
  expect(() => alwaysThrows('let x = [true, 0.0, 5];')).toThrow();
});

test('throws when array literal is empty', () => {
  expect(() => alwaysThrows('let x = [];')).toThrow();
});

test('typed empty Bool array becomes bool x[0] = {}', () => {
  expect(alwaysThrows('let x : [Bool; 0] = [];')).toBe('#include <stdbool.h>' + '\r\n' + 'bool x[0] = {};');
});

test('throws when typed Bool array is initialized with numeric literals', () => {
  expect(() => alwaysThrows('let x : [Bool; 3] = [1, 2, 3];')).toThrow();
});

test('throws when typed Bool array initializer length mismatches declared length', () => {
  expect(() => alwaysThrows('let x : [Bool; 3] = [1, 2];')).toThrow();
});

test('typed F32 array with integer initializers becomes float array', () => {
  expect(alwaysThrows('let x : [F32; 3] = [1, 2, 3];')).toBe('float x[3] = {1, 2, 3};');
});

test('typed F32 array accepts mixed int and float initializers', () => {
  expect(alwaysThrows('let x : [F32; 3] = [1, 2.0, 3];')).toBe('float x[3] = {1, 2.0, 3};');
});