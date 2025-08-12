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

	// Supported type mapping
	const typeMap: Record<string, string> = {
		I8: 'int8_t',
		I16: 'int16_t',
		I32: 'int32_t',
		I64: 'int64_t',
		U8: 'uint8_t',
		U16: 'uint16_t',
		U32: 'uint32_t',
		U64: 'uint64_t',
	};

	let varName = '';
	let value = '';
	let cType = 'int32_t'; // default type
	if (declaration.includes(':')) {
		const colonIdx = declaration.indexOf(':');
		varName = declaration.slice(0, colonIdx).trim();
		const afterColon = declaration.slice(colonIdx + 1).trim();
		const typeEndIdx =
			afterColon.indexOf('=') !== -1 ? afterColon.indexOf('=') : afterColon.length;
		const typeStr = afterColon.slice(0, typeEndIdx).trim();
		if (!typeMap[typeStr]) {
			throw new Error('Unsupported input');
		}
		cType = typeMap[typeStr];
		const eqIdx = afterColon.indexOf('=');
		if (eqIdx === -1) {
			throw new Error('Unsupported input');
		}
		value = afterColon.slice(eqIdx + 1).trim();
		return `${cType} ${varName} = ${value};`;
	} else {
		const eqIdx = declaration.indexOf('=');
		if (eqIdx === -1) {
			throw new Error('Unsupported input');
		}
		varName = declaration.slice(0, eqIdx).trim();
		value = declaration.slice(eqIdx + 1).trim();
		return `${cType} ${varName} = ${value};`;
	}
}
