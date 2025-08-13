const typeMap = {
  'U8': 'uint8_t',
  'U16': 'uint16_t',
  'U32': 'uint32_t',
  'U64': 'uint64_t',
  'I8': 'int8_t',
  'I16': 'int16_t',
  'I32': 'int32_t',
  'I64': 'int64_t',
  'Bool': 'bool'
};

function parseTypeSuffix(value) {
  for (const t of Object.keys(typeMap)) {
    if (value.endsWith(t)) {
      return { value: value.slice(0, value.length - t.length).trim(), type: t };
    }
  }
  // Handle true/false literals for Bool
  if (value === 'true' || value === 'false') {
    return { value: value, type: 'Bool' };
  }
  return { value: value, type: null };
}

function handleTypeAnnotation(rest) {
  const [left, right] = rest.split('=');
  const leftParts = left.split(':');
  const varName = leftParts[0].trim();
  const declaredType = leftParts[1].trim();
  let { value, type: valueType } = parseTypeSuffix(right.trim());
  if (valueType && declaredType !== valueType) {
    throw new Error('Type mismatch between declared and literal type');
  }
  if (!typeMap[declaredType]) {
    throw new Error("Unsupported type.");
  }
  // For Bool, ensure value is true/false
  if (declaredType === 'Bool' && value !== 'true' && value !== 'false') {
    throw new Error('Bool type must be assigned true or false');
  }
  return `${typeMap[declaredType]} ${varName} = ${value};`;
}

function handleNoTypeAnnotation(rest) {
  const eqIdx = rest.indexOf('=');
  if (eqIdx === -1) {
    throw new Error("Unsupported input format.");
  }
  const varName = rest.slice(0, eqIdx).trim();
  let { value, type } = parseTypeSuffix(rest.slice(eqIdx + 1).trim());
  if (type) {
    // For Bool, ensure value is true/false
    if (type === 'Bool' && value !== 'true' && value !== 'false') {
      throw new Error('Bool type must be assigned true or false');
    }
    return `${typeMap[type]} ${varName} = ${value};`;
  }
  return `int32_t ${varName} = ${value};`;
}

function compile(input) {
  if (typeof input === 'string' && input.length === 0) {
    return "Input was empty.";
  }
  const line = input.trim().replace(/;$/, '');
  if (line === '') {
    return "Input was empty.";
  }
  if (!line.startsWith('let ')) {
    throw new Error("Unsupported input format.");
  }
  const rest = line.slice(4);
  if (rest.includes(':')) {
    return handleTypeAnnotation(rest);
  }
  return handleNoTypeAnnotation(rest);
}

module.exports = compile;
