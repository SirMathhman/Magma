// Test for alwaysThrow function
import { alwaysThrow } from './alwaysThrow';

describe('alwaysThrow', () => {
  it('should always throw an error', () => {
    expect(() => alwaysThrow()).toThrow();
  });
});