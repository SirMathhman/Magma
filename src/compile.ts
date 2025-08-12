export function compile(input: string): string {
	if (input === '') {
		return '';
	}
	// Transform 'let x = 100;' to 'int32_t x = 100;'
	const letPattern = /^let\s+(\w+)\s*=\s*(.+);$/;
	const match = input.match(letPattern);
	if (match) {
		const varName = match[1];
		const value = match[2];
		return `int32_t ${varName} = ${value};`;
	}
	throw new Error('Unsupported input');
}
