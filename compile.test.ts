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
});