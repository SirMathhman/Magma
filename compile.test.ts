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
    expect(compile("let x : I32 = 0;")).toBe("int32_t x = 0;");
  });
});
