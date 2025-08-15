import { compile } from "./compile";

describe("The compiler", () => {
  const c = (s: string) => compile(s).replace(/\r\n/g, '\n');

  test("returns empty string for empty input", () => {
    expect(compile("")).toBe("");
  });

  test("throws for non-empty input", () => {
    expect(() => compile("hello")).toThrow(Error);
  });

  test("compiles simple let declaration", () => {
    expect(c("let x = 10;"))
      .toBe("#include <stdint.h>\nint32_t x = 10;");
  });

  test("compiles multiple lets with a single include", () => {
    expect(c("let x = 0; let y = 1;"))
      .toBe("#include <stdint.h>\nint32_t x = 0; int32_t y = 1;");
  });

  test("compiles typed let declaration", () => {
    expect(c("let x : I32 = 0;"))
      .toBe("#include <stdint.h>\nint32_t x = 0;");
  });

  test("compiles all supported integer typed lets", () => {
    const unsigned = ["U8", "U16", "U32", "U64"];
    const signed = ["I8", "I16", "I32", "I64"];

    const unsignedMap: { [k: string]: string } = { U8: 'uint8_t', U16: 'uint16_t', U32: 'uint32_t', U64: 'uint64_t' };
    for (const t of unsigned) {
      const expected = `#include <stdint.h>\n${unsignedMap[t]} a = 1;`;
      expect(c(`let a : ${t} = 1;`)).toBe(expected);
    }

    const signedMap: { [k: string]: string } = { I8: 'int8_t', I16: 'int16_t', I32: 'int32_t', I64: 'int64_t' };
    for (const t of signed) {
      const expected = `#include <stdint.h>\n${signedMap[t]} b = -2;`;
      expect(c(`let b : ${t} = -2;`)).toBe(expected);
    }
  });

  test("accepts matching literal suffix", () => {
    expect(c("let x : I32 = 0I32;"))
      .toBe("#include <stdint.h>\nint32_t x = 0;");
  });

  test("rejects mismatched literal suffix", () => {
    expect(() => compile("let x : I32 = 0U64;")).toThrow(Error);
  });

  test("compiles floating typed lets to C float/double", () => {
    expect(compile("let x : F32 = 0.0;")).toBe("float x = 0.0;");
    expect(compile("let x : F64 = 0.0;")).toBe("double x = 0.0;");
  });

  test("accepts float literal suffix matching typed float", () => {
    expect(compile("let x : F32 = 0.0F32;")).toBe("float x = 0.0;");
    expect(compile("let x : F64 = 0.0F64;")).toBe("double x = 0.0;");
  });

  test("rejects mismatched float literal suffix", () => {
    expect(() => compile("let x : F32 = 0.0F64;")).toThrow(Error);
  });

  test("untagged float literal with suffix infers proper float type", () => {
    expect(compile("let x = 0.0F64;")).toBe("double x = 0.0;");
  });

  test("untagged float literal defaults to F32", () => {
    expect(compile("let x = 0.0;")).toBe("float x = 0.0;");
  });

  test("reject int literal assigned to float type", () => {
    expect(() => compile("let x : F32 = 1;")).toThrow(Error);
  });

  test("reject float literal assigned to int type", () => {
    expect(() => compile("let x : I32 = 1.0;")).toThrow(Error);
  });

  test("allows assignment to mut variable", () => {
    expect(c("let mut x = 0; x = 1;"))
      .toBe("#include <stdint.h>\nint32_t x = 0; x = 1;");
  });

  test("rejects assignment to immutable variable", () => {
    expect(() => compile("let x = 0; x = 1;"))
      .toThrow(Error);
  });

  test("compiles fn with I32 return and return statement", () => {
    expect(compile("fn get() : I32 => {return 0;}"))
      .toBe("#include <stdint.h>\nint32_t get(){return 0;}");
  });

  test("accepts whitespace-only function body for Void", () => {
    const multi = `fn isLetter() : Void => {\n}`;
    expect(compile(multi)).toBe("void isLetter(){}");
  });

  test("compiles fn with one I32 parameter", () => {
    const out = compile("fn accept(value : I32) : Void => {}");
    expect(out.replace(/\r\n/g, '\n'))
      .toBe("#include <stdint.h>\nvoid accept(int32_t value){}");
  });

  test("compiles fn with *CStr parameter to char*", () => {
    expect(compile("fn accept(value : *CStr) : Void => {}"))
      .toBe("void accept(char* value){}");
  });

  test("compiles boolean comparisons using operators", () => {
    const ops = ['==', '!=', '<', '>', '<=', '>='];
    for (const op of ops) {
      const src = `let x : Bool = 3 ${op} 5;`;
      const out = c(src);
      expect(out).toBe('#include <stdbool.h>\nbool x = 3 ' + op + ' 5;');
    }
  });

  test("accepts basic if statement with parentheses and braces", () => {
    expect(compile('if(true){}')).toBe('#include <stdbool.h>\nif(true){}');
  });

  test("allows identifiers in if condition when symbol table has Bool", () => {
    const src = 'let x : Bool = true; if(x){}';
    expect(compile(src)).toBe('#include <stdbool.h>\nbool x = true; if(x){}');
  });

  test("accepts if inside void function with literal condition", () => {
    const src = 'fn doIt() : Void => { if(true){} }';
    expect(compile(src)).toBe('#include <stdbool.h>\nvoid doIt(){if(true){}}');
  });

  test("accepts if inside void function with parameter identifier condition", () => {
    const src = 'fn check(flag : Bool) : Void => { if(flag){} }';
    expect(compile(src)).toBe('#include <stdbool.h>\nvoid check(bool flag){if(flag){}}');
  });

  test("accepts return statement inside if for integer function", () => {
    const src = 'fn get() : I32 => { if(true){ return 0; } }';
    expect(compile(src)).toBe('#include <stdbool.h>\n#include <stdint.h>\nint32_t get(){if(true){return 0;}}');
  });

  test("accepts return inside if with identifier condition using parameter", () => {
    const src = 'fn get(x : I32) : I32 => { if(x == 0){ return 1; } }';
    expect(compile(src)).toBe('#include <stdint.h>\nint32_t get(int32_t x){if(x == 0){return 1;}}');
  });

  test("allows let statements inside function bodies", () => {
    const src = 'fn compute() : I32 => { let a : I32 = 5; return a; }';
    expect(compile(src)).toBe('#include <stdint.h>\nint32_t compute(){int32_t a = 5;return a;}');
  });

  test("compiles fixed-size integer array typed let", () => {
    const src = 'let x : [U8; 3] = [1, 2, 3];';
    const out = compile(src).replace(/\r\n/g, '\n');
    expect(out).toBe('#include <stdint.h>\nuint8_t x[3] = {1, 2, 3};');
  });

  test("extern function declarations emit nothing", () => {
    const src = 'extern fn doSomething() : Void;';
    expect(compile(src)).toBe('');
  });
});
