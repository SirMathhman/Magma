import alwaysThrows from '../src/alwaysThrows';

test('alwaysThrows throws with expected message', () => {
  expect(() => alwaysThrows()).toThrow('This function always throws');
});
