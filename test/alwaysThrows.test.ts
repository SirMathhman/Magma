import alwaysThrows from '../src/alwaysThrows';

test('returns empty string when given empty input', () => {
  expect(alwaysThrows('')).toBe('');
});

test('throws when given non-empty input', () => {
  expect(() => alwaysThrows('hello')).toThrow('This function always throws');
});
