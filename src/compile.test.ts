import { compile } from './compile';

describe('compile', () => {
	it("transforms 'let value : Bool = false;' to 'bool value = false;'", () => {
		expect(compile('let value : Bool = false;')).toBe('bool value = false;');
	});
	it("transforms 'let value : Bool = true;' to 'bool value = true;'", () => {
		expect(compile('let value : Bool = true;')).toBe('bool value = true;');
	});
	it("throws an error for assigning float to int type: 'let x : I32 = 0.4;'", () => {
		expect(() => compile('let x : I32 = 0.4;')).toThrow('Unsupported input');
	});
	it("transforms 'let x : F64 = 0;' to 'double x = 0;'", () => {
		expect(compile('let x : F64 = 0;')).toBe('double x = 0;');
	});
	it("transforms 'let x : F32 = 10;' to 'float x = 10;'", () => {
		expect(compile('let x : F32 = 10;')).toBe('float x = 10;');
	});
	it("transforms 'let x : F64 = 0.0;' to 'double x = 0.0;'", () => {
		expect(compile('let x : F64 = 0.0;')).toBe('double x = 0.0;');
	});
	it("transforms 'let x : F32 = 0.0;' to 'float x = 0.0;'", () => {
		expect(compile('let x : F32 = 0.0;')).toBe('float x = 0.0;');
	});
	it("throws an error for mismatched explicit and value types: 'let x : I32 = 0U8;'", () => {
		expect(() => compile('let x : I32 = 0U8;')).toThrow('Unsupported input');
	});
	it("transforms 'let x : I32 = 0I32;' to 'int32_t x = 0;'", () => {
		expect(compile('let x : I32 = 0I32;')).toBe('int32_t x = 0;');
	});
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
		it(`transforms 'let x = 123${magma};' to '${c} x = 123;'`, () => {
			expect(compile(`let x = 123${magma};`)).toBe(`${c} x = 123;`);
		});
	});

	it("transforms 'let value : U8 = 'a';' to 'uint8_t value = 'a';'", () => {
		expect(compile("let value : U8 = 'a';")).toBe("uint8_t value = 'a';");
	});

	it('throws an error for unsupported input', () => {
		expect(() => compile('hello')).toThrow('Unsupported input');
		expect(() => compile(' ')).toThrow('Unsupported input');
		expect(() => compile('test')).toThrow('Unsupported input');
	});
	it("transforms 'let array : [U8; 3] = [1, 2, 3];' to 'uint8_t array[3] = {1, 2, 3};'", () => {
		expect(compile('let array : [U8; 3] = [1, 2, 3];')).toBe('uint8_t array[3] = {1, 2, 3};');
	});
	it("infers type for 'let array = [1, 2, 3];' as 'uint8_t array[3] = {1, 2, 3};'", () => {
		expect(compile('let array = [1, 2, 3];')).toBe('uint8_t array[3] = {1, 2, 3};');
	});

	it("transforms 'let arr : [I16; 2] = [10, 20];' to 'int16_t arr[2] = {10, 20};'", () => {
		expect(compile('let arr : [I16; 2] = [10, 20];')).toBe('int16_t arr[2] = {10, 20};');
	});

	it("transforms 'let data : [U32; 4] = [100, 200, 300, 400];' to 'uint32_t data[4] = {100, 200, 300, 400};'", () => {
		expect(compile('let data : [U32; 4] = [100, 200, 300, 400];')).toBe(
			'uint32_t data[4] = {100, 200, 300, 400};'
		);
	});

	it("transforms 'let values : [F32; 2] = [1.5, 2.5];' to 'float values[2] = {1.5, 2.5};'", () => {
		expect(compile('let values : [F32; 2] = [1.5, 2.5];')).toBe(
			'float values[2] = {1.5, 2.5};'
		);
	});

	it("transforms 'let flags : [Bool; 3] = [true, false, true];' to 'bool flags[3] = {true, false, true};'", () => {
		expect(compile('let flags : [Bool; 3] = [true, false, true];')).toBe(
			'bool flags[3] = {true, false, true};'
		);
	});

	it("transforms 'let x : [U8; 2] = [9, 8];' to 'uint8_t x[2] = {9, 8};'", () => {
		expect(compile('let x : [U8; 2] = [9, 8];')).toBe('uint8_t x[2] = {9, 8};');
	});

	it('transforms let word : [U8; 5] = "Hello"; to uint8_t word[5] = {72, 101, 108, 108, 111};', () => {
		expect(compile('let word : [U8; 5] = "Hello";')).toBe(
			'uint8_t word[5] = {72, 101, 108, 108, 111};'
		);
	});
	it('infers type for let word = "Hello"; as uint8_t word[5] = {72, 101, 108, 108, 111};', () => {
		expect(compile('let word = "Hello";')).toBe('uint8_t word[5] = {72, 101, 108, 108, 111};');
	});

	it("transforms 'let x = 100; let y = x;' to 'int32_t x = 100; int32_t y = x;'", () => {
		expect(compile('let x = 100; let y = x;')).toBe('int32_t x = 100; int32_t y = x;');
	});
	
	it('should allow reassignment for let mut variables', () => {
		const code = `let mut x = 100; x = 200;`;
		expect(() => compile(code)).not.toThrow();
	});

	it('should not allow reassignment for let variables without mut', () => {
		const code = `let x = 100; x = 200;`;
		expect(() => compile(code)).toThrow();
	});
});
