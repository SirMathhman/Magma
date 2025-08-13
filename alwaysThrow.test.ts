// Test for alwaysThrow function
import { alwaysThrow } from './alwaysThrow';

describe('alwaysThrow', () => {
  it('should return empty string if input is empty', () => {
    expect(alwaysThrow("")).toBe("");
  });

  it('should throw an error if input is not empty', () => {
    expect(() => alwaysThrow("something")).toThrow('Input was not empty');
  });
});