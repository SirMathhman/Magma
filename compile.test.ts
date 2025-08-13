// Test for alwaysThrow function
import { compile } from './compile';

describe('The compiler', () => {
  it('should return empty string if input is empty', () => {
    expect(compile("")).toBe("");
  });

  it('should throw an error if input is not empty', () => {
    expect(() => compile("something")).toThrow('Input was not empty');
  });
});