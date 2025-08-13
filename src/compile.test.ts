import { compile } from "./compile";

describe("compile", () => {
  it("should return an empty string if input is empty", () => {
    expect(compile("")).toBe("");
  });

  it("should throw an error for non-empty input", () => {
    expect(() => compile("test")).toThrow("This function always throws an error.");
  });
});
