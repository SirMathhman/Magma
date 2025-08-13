import { compile } from "./compile";

describe("compile", () => {
  it("should throw an error every time it is called", () => {
    expect(() => compile()).toThrow("This function always throws an error.");
  });
});
