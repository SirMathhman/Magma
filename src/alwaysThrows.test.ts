import { alwaysThrows } from './alwaysThrows';

describe('alwaysThrows', () => {
  it('returns empty string when input is empty', () => {
    expect(alwaysThrows('')).toBe('');
  });

  it('throws an error when input is not empty', () => {
    expect(() => alwaysThrows('hello')).toThrow('This function always throws');
    expect(() => alwaysThrows(' ')).toThrow('This function always throws');
    expect(() => alwaysThrows('test')).toThrow('This function always throws');
  });
});
