const alwaysThrows = require('./alwaysThrows');

describe('alwaysThrows', () => {
  it('should return a string if input is empty', () => {
    expect(alwaysThrows('')).toBe('Input was empty.');
  });

  it('should throw an error if input is not empty', () => {
    expect(() => alwaysThrows('not empty')).toThrow('This function always throws an error unless the input is an empty string.');
  });
});