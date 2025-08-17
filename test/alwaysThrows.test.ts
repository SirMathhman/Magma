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