const alwaysThrows = require('./alwaysThrows');

describe('alwaysThrows', () => {
  it('should throw an error every time it is called', () => {
    expect(() => alwaysThrows()).toThrow('This function always throws an error.');
  });
});
