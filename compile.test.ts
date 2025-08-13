// Test for alwaysThrow function
import { compile } from './compile';

describe('The compiler', () => {
  it('should return empty string if input is empty', () => {
    expect(compile("")).toBe("");
  });
  it('should throw an error if input is not empty', () => {
    expect(() => compile("something")).toThrow('Input was not empty');
  });
  const cases = [
    ["I8", "int8_t"],
    ["I16", "int16_t"],
    ["I32", "int32_t"],
    ["I64", "int64_t"],
    ["U8", "uint8_t"],
    ["U16", "uint16_t"],
    ["U32", "uint32_t"],
    ["U64", "uint64_t"],
  ];
  cases.forEach(([type, cType], idx) => {
    it(`should compile let v : ${type} = ${idx + 1}; to ${cType} v = ${idx + 1};`, () => {
      const input = `let v : ${type} = ${idx + 1};`;
      const expected = `${cType} v = ${idx + 1};`;
      expect(compile(input)).toBe(expected);
    });
  });
  it('should allow assignment of true to Bool', () => {
    // This should fail until Bool is implemented
    expect(() => compile('let x : Bool = true;')).not.toThrow();
  });
  it('should allow assignment of false to Bool', () => {
    // This should fail until Bool is implemented
    expect(() => compile('let y : Bool = false;')).not.toThrow();
  });
  it("should compile let x : U8 = 'a'; to uint8_t x = 'a';", () => {
    expect(compile("let x : U8 = 'a';")).toBe("uint8_t x = 'a';");
  });
  it("should throw for let x : Bool = 100;", () => {
    expect(() => compile("let x : Bool = 100;")).toThrow();
  });
  it("should infer int32_t for let x = 100; let y = x; and produce int32_t x = 100; int32_t y = x;", () => {
    const input = "let x = 100; let y = x;";
    const expected = "int32_t x = 100; int32_t y = x;";
    expect(compile(input)).toBe(expected);
  });
  it("should compile fn empty() : Void => {} to void empty(){}", () => {
    const input = "fn empty() : Void => {}";
    const expected = "void empty(){}";
    expect(compile(input)).toBe(expected);
  });
});