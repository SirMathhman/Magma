function isValidArrayType(
	typeMap: Record<string, string>,
	elemType: string,
	arrLen: string
): boolean {
	return !!typeMap[elemType] && !!arrLen && !isNaN(Number(arrLen));
}

function getArrayAssignmentValue(arrayElemType: string, value: string): string {
	if (arrayElemType === 'U8' && value.startsWith('"') && value.endsWith('"')) {
		return stringToCharCodes(value.slice(1, -1));
	} else if (value.startsWith('[') && value.endsWith(']')) {
		return value.slice(1, -1).trim();
	}
	return value;
}
function stringToCharCodes(str: string): string {
	return Array.from(str)
		.map((c) => String(c).charCodeAt(0))
		.join(', ');
}
type TypeSuffix = 'U8' | 'U16' | 'U32' | 'U64' | 'I8' | 'I16' | 'I32' | 'I64';
function getArrayTypeParts(typeStr: string): { elemType: string; arrLen: string } {
	if (!(typeStr.startsWith('[') && typeStr.endsWith(']'))) throw new Error('Unsupported input');
	const inner = typeStr.slice(1, -1).trim();
	const semiIdx = inner.indexOf(';');
	if (semiIdx === -1) throw new Error('Unsupported input');
	const elemType = inner.slice(0, semiIdx).trim();
	const arrLen = inner.slice(semiIdx + 1).trim();
	return { elemType, arrLen };
}

function parseArrayType(
	typeStr: string,
	afterColon: string,
	varName: string,
	typeMap: Record<string, string>,
	variableTypes: Map<string, string> = new Map()
): string {
	let arrayParts;
	try {
		arrayParts = getArrayTypeParts(typeStr);
	} catch {
		throw new Error('Unsupported input');
	}
	const arrayElemType = arrayParts.elemType;
	const arrayLen = arrayParts.arrLen;
	if (!isValidArrayType(typeMap, arrayElemType, arrayLen)) {
		throw new Error('Unsupported input');
	}
	if (!varName) {
		throw new Error('Unsupported input');
	}
	const cType = typeMap[arrayElemType];
	const eqIdx = afterColon.indexOf('=');
	if (eqIdx === -1) {
		throw new Error('Unsupported input');
	}
	let value = afterColon.slice(eqIdx + 1).trim();
	value = getArrayAssignmentValue(arrayElemType, value);

	// Track the array type (note: this is a simplified representation)
	variableTypes.set(varName, `${cType}[${arrayLen}]`);

	return `${cType} ${varName}[${arrayLen}] = {${value}};`;
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

function parseTypedDeclaration(
	declaration: string,
	variableTypes: Map<string, string> = new Map()
): string {
	const typeMap = getTypeMap();
	const colonIdx = declaration.indexOf(':');
	const varName = declaration.slice(0, colonIdx).trim();
	if (!varName) {
		throw new Error('Unsupported input');
	}
	const afterColon = declaration.slice(colonIdx + 1).trim();

	const typeEndIdx = afterColon.indexOf('=') !== -1 ? afterColon.indexOf('=') : afterColon.length;
	const typeStr = afterColon.slice(0, typeEndIdx).trim();

	// Handle array type syntax: [U8; 3] without regex
	if (typeStr.startsWith('[') && typeStr.endsWith(']')) {
		return parseArrayType(typeStr, afterColon, varName, typeMap, variableTypes);
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

	variableTypes.set(varName, cType);
	return `${cType} ${varName} = ${cleanValue};`;
}

function handleArrayInference(
	value: string,
	varName: string,
	typeMap: Record<string, string>
): string {
	if (value.startsWith('[') && value.endsWith(']')) {
		// Remove brackets and split values
		const arrValues = value
			.slice(1, -1)
			.split(',')
			.map((v) => v.trim());
		// Infer type: if all are integers 0-255, use U8; else use int32_t
		const isU8 = arrValues.every((v) => /^\d+$/.test(v) && Number(v) >= 0 && Number(v) <= 255);
		const elemType = isU8 ? 'U8' : 'I32';
		const cType = typeMap[elemType];
		const arrLen = arrValues.length;
		return `${cType} ${varName}[${arrLen}] = {${arrValues.join(', ')}};`;
	}
	return '';
}

function handleStringInference(
	value: string,
	varName: string,
	typeMap: Record<string, string>
): string {
	if (value.startsWith('"') && value.endsWith('"')) {
		const str = value.slice(1, -1);
		const arrValues = Array.from(str).map((c) => c.charCodeAt(0));
		const cType = typeMap['U8'];
		const arrLen = arrValues.length;
		return `${cType} ${varName}[${arrLen}] = {${arrValues.join(', ')}};`;
	}
	return '';
}

function parseUntypedDeclaration(
	declaration: string,
	variableTypes: Map<string, string> = new Map()
): string {
	const typeMap = getTypeMap();
	const eqIdx = declaration.indexOf('=');

	if (eqIdx === -1) {
		throw new Error('Unsupported input');
	}

	const varName = declaration.slice(0, eqIdx).trim();
	let value = declaration.slice(eqIdx + 1).trim();

	// Check if value is a variable reference
	if (variableTypes.has(value)) {
		const cType = variableTypes.get(value)!;
		variableTypes.set(varName, cType);
		return `${cType} ${varName} = ${value};`;
	}

	// Array detection
	const arrayResult = handleArrayInference(value, varName, typeMap);
	if (arrayResult) {
		return arrayResult;
	}

	// String to U8 array
	const stringResult = handleStringInference(value, varName, typeMap);
	if (stringResult) {
		return stringResult;
	}

	let cType = 'int32_t'; // default type
	const { value: cleanValue, suffix } = detectAndRemoveSuffix(value);
	if (suffix) {
		if (!typeMap[suffix]) {
			throw new Error('Unsupported input');
		}
		cType = typeMap[suffix];
		value = cleanValue;
	}

	variableTypes.set(varName, cType);
	return `${cType} ${varName} = ${value};`;
}

function handleStringCharacter(
	char: string,
	inString: boolean,
	stringChar: string,
	i: number,
	input: string
): { inString: boolean; stringChar: string } {
	if (!inString && (char === '"' || char === "'")) {
		return { inString: true, stringChar: char };
	}
	if (inString && char === stringChar && (i === 0 || input[i - 1] !== '\\')) {
		return { inString: false, stringChar: '' };
	}
	return { inString, stringChar };
}

function splitStatements(input: string): string[] {
	const statements: string[] = [];
	let current = '';
	let bracketDepth = 0;
	let inString = false;
	let stringChar = '';

	for (let i = 0; i < input.length; i++) {
		const char = input[i];

		const stringState = handleStringCharacter(char, inString, stringChar, i, input);
		inString = stringState.inString;
		stringChar = stringState.stringChar;

		if (!inString) {
			if (char === '[') {
				bracketDepth++;
			} else if (char === ']') {
				bracketDepth--;
			} else if (char === ';' && bracketDepth === 0) {
				const trimmed = current.trim();
				if (trimmed.length > 0) {
					statements.push(trimmed);
				}
				current = '';
				continue;
			}
		}

		current += char;
	}

	const trimmed = current.trim();
	if (trimmed.length > 0) {
		statements.push(trimmed);
	}

	return statements;
}

export function compile(input: string): string {
	if (input === '') {
		return '';
	}

	const trimmed = input.trim();

	if (trimmed === '') {
		throw new Error('Unsupported input');
	}

	const statements = splitStatements(trimmed);
	const variableTypes = new Map<string, string>();
	const mutability = new Map<string, boolean>();
	const results: string[] = [];

	for (const statement of statements) {
		// Handle mutability
		if (statement.startsWith('let mut ')) {
			const declaration = statement.slice(8).trim();
			let varName: string;
			if (declaration.includes(':')) {
				varName = declaration.split(':')[0].trim();
				results.push(parseTypedDeclaration(declaration, variableTypes));
			} else {
				varName = declaration.split('=')[0].trim();
				results.push(parseUntypedDeclaration(declaration, variableTypes));
			}
			mutability.set(varName, true);
			continue;
		}
		if (statement.startsWith('let ')) {
			const declaration = statement.slice(4).trim();
			let varName: string;
			if (declaration.includes(':')) {
				varName = declaration.split(':')[0].trim();
				results.push(parseTypedDeclaration(declaration, variableTypes));
			} else {
				varName = declaration.split('=')[0].trim();
				results.push(parseUntypedDeclaration(declaration, variableTypes));
			}
			mutability.set(varName, false);
			continue;
		}
		// Assignment (re-assignment)
		if (statement.includes('=') && !statement.startsWith('let')) {
			const [varName, value] = statement.split('=').map((s) => s.trim());
			if (!mutability.has(varName)) {
				throw new Error('Unsupported input');
			}
			if (!mutability.get(varName)) {
				throw new Error('Unsupported input');
			}
			// For simplicity, just allow reassignment if mut is true
			// No type checking for reassignment in this implementation
			results.push(`${varName} = ${value};`);
			continue;
		}
		throw new Error('Unsupported input');
	}

	return results.join(' ');
}
