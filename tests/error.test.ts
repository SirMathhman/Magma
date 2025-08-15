import { transformIfEmpty } from '../src/error';

test('transformIfEmpty throws on non-empty input', () => {
  expect(() => transformIfEmpty('not empty')).toThrow('Input must be empty');
});

test('transformIfEmpty returns starter C code for empty input', () => {
  const out = transformIfEmpty('');
  expect(out).toContain('generated C');
  expect(out).toContain('int main');
});
