// Test for alwaysThrow function
import { compile } from './compile';

describe('The compiler', () => {
  it('should return empty string if input is empty', () => {
    expect(compile("")).toBe("");
  });
  it('should throw an error if input is not empty', () => {
    expect(() => compile("something")).toThrow('Input was not empty');
  });
  it('should compile let x : I32 = 100; to int32_t x = 100;', () => {
    const input = "let x : I32 = 100;";
    const expected = "int32_t x = 100;";
    expect(compile(input)).toBe(expected);
  });
});