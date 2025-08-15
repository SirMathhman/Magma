import { compile } from "./compile";

describe("The compiler", () => {
  test("returns empty string for empty input", () => {
    expect(compile("")).toBe("");
  });

  test("throws for non-empty input", () => {
    expect(() => compile("hello")).toThrow(Error);
  });

  test("compiles simple let declaration", () => {
    expect(compile("let x = 10;")).toBe("#include <stdint.h>\nint32_t x = 10;");
  });

  test("compiles typed let declaration", () => {
    expect(compile("let x : I32 = 0;")).toBe("#include <stdint.h>\nint32_t x = 0;");
  });

  test("compiles all supported integer typed lets", () => {
    const unsigned = ["U8", "U16", "U32", "U64"];
    const signed = ["I8", "I16", "I32", "I64"];

    const unsignedMap: { [k: string]: string } = { U8: 'uint8_t', U16: 'uint16_t', U32: 'uint32_t', U64: 'uint64_t' };
    for (const t of unsigned) {
      const expected = `#include <stdint.h>\n${unsignedMap[t]} a = 1;`;
      expect(compile(`let a : ${t} = 1;`)).toBe(expected);
    }

    const signedMap: { [k: string]: string } = { I8: 'int8_t', I16: 'int16_t', I32: 'int32_t', I64: 'int64_t' };
    for (const t of signed) {
      const expected = `#include <stdint.h>\n${signedMap[t]} b = -2;`;
      expect(compile(`let b : ${t} = -2;`)).toBe(expected);
    }
  });

  test("accepts matching literal suffix", () => {
    expect(compile("let x : I32 = 0I32;")).toBe("#include <stdint.h>\nint32_t x = 0;");
  });

  test("rejects mismatched literal suffix", () => {
    expect(() => compile("let x : I32 = 0U64;")).toThrow(Error);
  });

  test("compiles floating typed lets to C float/double", () => {
    expect(compile("let x : F32 = 0.0;")).toBe("float x = 0.0;");
    expect(compile("let x : F64 = 0.0;")).toBe("double x = 0.0;");
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
});
