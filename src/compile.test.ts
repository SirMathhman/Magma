import { compile } from "./compile";

describe("compile", () => {
  it("should compile a function with a single parameter", () => {
    expect(compile("fn consume(value : I32) : Void => {}"))
      .toBe("void consume(int value){}");
  });
  it("should return an empty string if input is empty", () => {
    expect(compile("")).toBe("");
  });

  it("should throw an error for non-empty input", () => {
    expect(() => compile("test")).toThrow();
  });

  it("should compile a simple empty function", () => {
    expect(compile("fn empty() : Void => {}"))
      .toBe("void empty(){}");
  });

  it("should compile a function returning int", () => {
    expect(compile("fn get() : I32 => {return 0;}"))
      .toBe("int get(){return 0;}");
  });
});
