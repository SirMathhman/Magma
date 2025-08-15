import { alwaysError } from '../src/error';

test('alwaysError throws an Error', () => {
  expect(() => alwaysError()).toThrow('This function always errors');
});
