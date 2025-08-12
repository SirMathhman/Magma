import { compile } from './compile';

describe('compile', () => {
	it('returns empty string when input is empty', () => {
		expect(compile('')).toBe('');
	});

	it("transforms 'let x = 100;' to 'int32_t x = 100;'", () => {
		expect(compile('let x = 100;')).toBe('int32_t x = 100;');
	});

	it("transforms 'let y = 42;' to 'int32_t y = 42;'", () => {
		expect(compile('let y = 42;')).toBe('int32_t y = 42;');
	});

	it("transforms 'let x = 7;' to 'int32_t x = 7;'", () => {
		expect(compile('let x = 7;')).toBe('int32_t x = 7;');
	});

	const typeCases = [
		{ magma: 'I8', c: 'int8_t' },
		{ magma: 'I16', c: 'int16_t' },
		{ magma: 'I32', c: 'int32_t' },
		{ magma: 'I64', c: 'int64_t' },
		{ magma: 'U8', c: 'uint8_t' },
		{ magma: 'U16', c: 'uint16_t' },
		{ magma: 'U32', c: 'uint32_t' },
		{ magma: 'U64', c: 'uint64_t' },
	];
	typeCases.forEach(({ magma, c }) => {
		it(`transforms 'let x : ${magma} = 123;' to '${c} x = 123;'`, () => {
			expect(compile(`let x : ${magma} = 123;`)).toBe(`${c} x = 123;`);
		});
	});

	it('throws an error for unsupported input', () => {
		expect(() => compile('hello')).toThrow('Unsupported input');
		expect(() => compile(' ')).toThrow('Unsupported input');
		expect(() => compile('test')).toThrow('Unsupported input');
	});
});
