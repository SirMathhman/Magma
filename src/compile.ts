function synthesizeVarName(typeStr: string, typeMap: Record<string, string>): string {
	const cTypeName = typeMap[typeStr] || '';
	if (cTypeName.endsWith('_t')) {
		return `${cTypeName.slice(0, -2)}_x`;
	}
	return `${cTypeName}_x`;
}
type TypeSuffix = 'U8' | 'U16' | 'U32' | 'U64' | 'I8' | 'I16' | 'I32' | 'I64';

function parseArrayType(
	typeStr: string,
	afterColon: string,
	varName: string,
	typeMap: Record<string, string>
): string {
	if (!(typeStr.startsWith('[') && typeStr.endsWith(']'))) return '';
	const inner = typeStr.slice(1, -1).trim();
	const semiIdx = inner.indexOf(';');
	if (semiIdx === -1) {
		throw new Error('Unsupported input');
	}
	const elemType = inner.slice(0, semiIdx).trim();
	const arrLen = inner.slice(semiIdx + 1).trim();
	if (!typeMap[elemType] || !arrLen || isNaN(Number(arrLen))) {
		throw new Error('Unsupported input');
	}
	const cType = typeMap[elemType];
	const eqIdx = afterColon.indexOf('=');
	if (eqIdx === -1) {
		throw new Error('Unsupported input');
	}
	let value = afterColon.slice(eqIdx + 1).trim();
	// Remove brackets from array value
	if (value.startsWith('[') && value.endsWith(']')) {
		value = value.slice(1, -1).trim();
	}
	return `${cType} ${varName}[${arrLen}] = {${value}};`;
}

function getTypeMap(): Record<string, string> {
	return {
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
}

function detectAndRemoveSuffix(value: string): { value: string; suffix: string | null } {
	const suffixes: TypeSuffix[] = ['U8', 'U16', 'U32', 'U64', 'I8', 'I16', 'I32', 'I64'];

	for (const suffix of suffixes) {
		if (value.endsWith(suffix)) {
			return {
				value: value.slice(0, value.length - suffix.length),
				suffix: suffix,
			};
		}
	}

	return { value, suffix: null };
}

function validateTypeAndValue(typeStr: string, value: string): void {
	const intTypes = ['I8', 'I16', 'I32', 'I64', 'U8', 'U16', 'U32', 'U64'];

	if (intTypes.includes(typeStr)) {
		if (value.includes('.') && value.match(/^\d*\.\d+$/)) {
			throw new Error('Unsupported input');
		}
	}
}

function parseTypedDeclaration(declaration: string): string {
	const typeMap = getTypeMap();
	const colonIdx = declaration.indexOf(':');
	let varName = declaration.slice(0, colonIdx).trim();
	// If varName is empty, synthesize a name based on type
	if (!varName) {
		const afterColon = declaration.slice(colonIdx + 1).trim();
		const typeEndIdx =
			afterColon.indexOf('=') !== -1 ? afterColon.indexOf('=') : afterColon.length;
		const typeStrSynth = afterColon.slice(0, typeEndIdx).trim();
		varName = synthesizeVarName(typeStrSynth, typeMap);
	}
	const afterColon = declaration.slice(colonIdx + 1).trim();

	const typeEndIdx = afterColon.indexOf('=') !== -1 ? afterColon.indexOf('=') : afterColon.length;
	const typeStr = afterColon.slice(0, typeEndIdx).trim();

	// Handle array type syntax: [U8; 3] without regex
	const arrayResult = parseArrayType(typeStr, afterColon, varName, typeMap);
	if (arrayResult) {
		return arrayResult;
	}
	// Regular type
	if (!typeMap[typeStr]) {
		throw new Error('Unsupported input');
	}
	const cType = typeMap[typeStr];
	const eqIdx = afterColon.indexOf('=');
	if (eqIdx === -1) {
		throw new Error('Unsupported input');
	}
	const value = afterColon.slice(eqIdx + 1).trim();
	const { value: cleanValue, suffix } = detectAndRemoveSuffix(value);
	if (suffix && suffix !== typeStr) {
		throw new Error('Unsupported input');
	}
	validateTypeAndValue(typeStr, cleanValue);
	// If varName is synthesized, use only that (e.g., uint8_x), not both type and varName
	if (declaration.slice(0, colonIdx).trim() === '') {
		return `${varName} = ${cleanValue};`;
	}
	return `${cType} ${varName} = ${cleanValue};`;
}

function parseUntypedDeclaration(declaration: string): string {
	const typeMap = getTypeMap();
	const eqIdx = declaration.indexOf('=');

	if (eqIdx === -1) {
		throw new Error('Unsupported input');
	}

	const varName = declaration.slice(0, eqIdx).trim();
	let value = declaration.slice(eqIdx + 1).trim();
	let cType = 'int32_t'; // default type

	const { value: cleanValue, suffix } = detectAndRemoveSuffix(value);

	if (suffix) {
		if (!typeMap[suffix]) {
			throw new Error('Unsupported input');
		}
		cType = typeMap[suffix];
		value = cleanValue;
	}

	return `${cType} ${varName} = ${value};`;
}
export function compile(input: string): string {
	if (input === '') {
		return '';
	}

	const trimmed = input.trim();

	if (trimmed === '') {
		throw new Error('Unsupported input');
	}

	if (!trimmed.startsWith('let ')) {
		throw new Error('Unsupported input');
	}

	const declaration = trimmed.slice(4, trimmed.length - (trimmed.endsWith(';') ? 1 : 0)).trim();

	if (declaration.includes(':')) {
		return parseTypedDeclaration(declaration);
	} else {
		return parseUntypedDeclaration(declaration);
	}
}
