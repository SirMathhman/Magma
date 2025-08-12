import { compile } from './compile';

describe('compile', () => {
	it('returns empty string when input is empty', () => {
		expect(compile('')).toBe('');
	});

	it("transforms 'let x = 100;' to 'int32_t x = 100;'", () => {
		expect(compile('let x = 100;')).toBe('int32_t x = 100;');
	});

	it('throws an error for unsupported input', () => {
		expect(() => compile('hello')).toThrow('Unsupported input');
		expect(() => compile(' ')).toThrow('Unsupported input');
		expect(() => compile('test')).toThrow('Unsupported input');
	});
});
