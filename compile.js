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

function handleArrayTypeAnnotation(varName, declaredType, right) {
  const inner = declaredType.slice(1, -1);
  const semiIdx = inner.indexOf(';');
  if (semiIdx === -1) throw new Error("Invalid array type annotation.");
  const elemType = inner.slice(0, semiIdx).trim();
  const arrLenStr = inner.slice(semiIdx + 1).trim();
  const arrLen = Number(arrLenStr);
  if (!typeMap[elemType]) {
    throw new Error("Unsupported array element type.");
  }
  if (!Number.isInteger(arrLen) || arrLen < 0) {
    throw new Error("Invalid array length.");
  }
  // Manual array value parsing: [1, 2, 3]
  const arrVal = right.trim();
  if (!arrVal.startsWith('[') || !arrVal.endsWith(']')) {
    throw new Error("Array value must be in brackets.");
  }
  const elemsStr = arrVal.slice(1, -1);
  const elems = elemsStr.split(',').map(e => e.trim()).filter(e => e.length > 0);
  if (elems.length !== arrLen) {
    throw new Error("Array length does not match type annotation.");
  }
  // Validate elements for type (only number for now)
  if (!elems.every(e => {
    if (e.length === 0) return false;
    // Only allow integer literals
    if (e[0] === '-' && e.length > 1) {
      return e.slice(1).split('').every(ch => ch >= '0' && ch <= '9');
    }
    return e.split('').every(ch => ch >= '0' && ch <= '9');
  })) {
    throw new Error("Array elements must be integers.");
  }
  return `${typeMap[elemType]} ${varName}[${arrLen}] = {${elems.join(', ')}};`;
}

function validateBoolAssignment(declaredType, value) {
  if (declaredType === 'Bool' && value !== 'true' && value !== 'false') {
    throw new Error('Bool type must be assigned true or false');
  }
}

function handleTypeAnnotation(rest) {
  const [left, right] = rest.split('=');
  const leftParts = left.split(':');
  const varName = leftParts[0].trim();
  const declaredType = leftParts[1].trim();
  // Manual array type parsing: [Type; len]
  if (declaredType.startsWith('[') && declaredType.endsWith(']') && declaredType.includes(';')) {
    return handleArrayTypeAnnotation(varName, declaredType, right);
  }
  let { value, type: valueType } = parseTypeSuffix(right.trim());
  if (valueType && declaredType !== valueType) {
    throw new Error('Type mismatch between declared and literal type');
  }
  if (!typeMap[declaredType]) {
    throw new Error("Unsupported type.");
  }
  validateBoolAssignment(declaredType, value);
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
  if (typeof input !== 'string' || input.trim().length === 0) {
    return "Input was empty.";
  }
  // Improved split: only split on semicolons not inside brackets
  function smartSplit(str) {
    let result = [];
    let buf = '';
    let bracketDepth = 0;
    for (let i = 0; i < str.length; ++i) {
      const ch = str[i];
      if (ch === '[') bracketDepth++;
      if (ch === ']') bracketDepth--;
      if (ch === ';' && bracketDepth === 0) {
        if (buf.trim().length > 0) result.push(buf.trim());
        buf = '';
      } else {
        buf += ch;
      }
    }
    if (buf.trim().length > 0) result.push(buf.trim());
    return result;
  }
  const statements = smartSplit(input);
  const results = statements.map(stmt => {
    if (!stmt.startsWith('let ')) {
      throw new Error("Unsupported input format.");
    }
    const rest = stmt.slice(4);
    if (rest.includes(':')) {
      return handleTypeAnnotation(rest);
    }
    return handleNoTypeAnnotation(rest);
  });
  // Ensure only single semicolons between statements
  return results.map(r => r.endsWith(';') ? r.slice(0, -1) : r).join('; ') + ';';
}

module.exports = compile;
