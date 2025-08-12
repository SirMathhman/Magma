export function compile(input: string): string {
	if (input === '') {
		return '';
	}
	// Remove trailing semicolon and trim
	const trimmed = input.trim();
	if (trimmed === '') {
		throw new Error('Unsupported input');
	}
	if (!trimmed.startsWith('let ')) {
		throw new Error('Unsupported input');
	}
	const declaration = trimmed.slice(4, trimmed.length - (trimmed.endsWith(';') ? 1 : 0)).trim();

	// Check for explicit type
	let varName = '';
	let value = '';
	if (declaration.includes(':')) {
		const colonIdx = declaration.indexOf(':');
		varName = declaration.slice(0, colonIdx).trim();
		const afterColon = declaration.slice(colonIdx + 1).trim();
		if (!afterColon.startsWith('I32')) {
			throw new Error('Unsupported input');
		}
		const eqIdx = afterColon.indexOf('=');
		if (eqIdx === -1) {
			throw new Error('Unsupported input');
		}
		value = afterColon.slice(eqIdx + 1).trim();
		return `int32_t ${varName} = ${value};`;
	} else {
		const eqIdx = declaration.indexOf('=');
		if (eqIdx === -1) {
			throw new Error('Unsupported input');
		}
		varName = declaration.slice(0, eqIdx).trim();
		value = declaration.slice(eqIdx + 1).trim();
		return `int32_t ${varName} = ${value};`;
	}
}
