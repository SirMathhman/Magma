import { compile } from "./compile";

describe("The compiler", () => {
  test("returns empty string for empty input", () => {
    expect(compile("")).toBe("");
  });

  test("throws for non-empty input", () => {
    expect(() => compile("hello")).toThrow(Error);
  });

  test("compiles simple let declaration", () => {
    expect(compile("let x = 10;")).toBe("int32_t x = 10;");
  });

  test("compiles typed let declaration", () => {
    expect(compile("let x : I32 = 0;")).toBe("let x : I32 = 0I32;");
  });

  test("compiles all supported integer typed lets", () => {
    const unsigned = ["U8", "U16", "U32", "U64"];
    const signed = ["I8", "I16", "I32", "I64"];

    for (const t of unsigned) {
      expect(compile(`let a : ${t} = 1;`)).toBe(`let a : ${t} = 1${t};`);
    }

    for (const t of signed) {
      expect(compile(`let b : ${t} = -2;`)).toBe(`let b : ${t} = -2${t};`);
    }
  });

  test("accepts matching literal suffix", () => {
    expect(compile("let x : I32 = 0I32;")).toBe("let x : I32 = 0I32;");
  });

  test("rejects mismatched literal suffix", () => {
    expect(() => compile("let x : I32 = 0U64;")).toThrow(Error);
  });
});
