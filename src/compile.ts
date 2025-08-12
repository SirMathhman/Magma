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
		F32: 'float',
		F64: 'double',
		Bool: 'bool',
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

		// Check for value suffix type, e.g. 0U8, 123I64, without regex
		const suffixes = ['U8', 'U16', 'U32', 'U64', 'I8', 'I16', 'I32', 'I64'];
		let foundSuffix: string | null = null;
		for (const suffix of suffixes) {
			if (value.endsWith(suffix)) {
				foundSuffix = suffix;
				value = value.slice(0, value.length - suffix.length);
				break;
			}
		}
		if (foundSuffix && foundSuffix !== typeStr) {
			throw new Error('Unsupported input');
		}

		// If explicit type is int, value must not be a float
		const intTypes = ['I8', 'I16', 'I32', 'I64', 'U8', 'U16', 'U32', 'U64'];
		if (intTypes.includes(typeStr)) {
			if (value.includes('.') && value.match(/^\d*\.\d+$/)) {
				throw new Error('Unsupported input');
			}
		}
		return `${cType} ${varName} = ${value};`;
	} else {
		const eqIdx = declaration.indexOf('=');
		if (eqIdx === -1) {
			throw new Error('Unsupported input');
		}
		varName = declaration.slice(0, eqIdx).trim();
		value = declaration.slice(eqIdx + 1).trim();

		// Check for value suffix type, e.g. 0U8, 123I64, without regex
		const suffixes = ['U8', 'U16', 'U32', 'U64', 'I8', 'I16', 'I32', 'I64'];
		for (const suffix of suffixes) {
			if (value.endsWith(suffix)) {
				const val = value.slice(0, value.length - suffix.length);
				if (!typeMap[suffix]) {
					throw new Error('Unsupported input');
				}
				cType = typeMap[suffix];
				value = val;
				break;
			}
		}
		return `${cType} ${varName} = ${value};`;
	}
}
