function compile(input) {
  if (typeof input === 'string' && input.length === 0) {
    return "Input was empty.";
  }
  const typeMap = {
    'U8': 'uint8_t',
    'U16': 'uint16_t',
    'U32': 'uint32_t',
    'U64': 'uint64_t',
    'I8': 'int8_t',
    'I16': 'int16_t',
    'I32': 'int32_t',
    'I64': 'int64_t'
  };

  // Remove trailing semicolon and trim
  const line = input.trim().replace(/;$/, '');
  if (line === '') {
    return "Input was empty.";
  }

  // Only handle 'let' declarations
  if (!line.startsWith('let ')) {
    throw new Error("Unsupported input format.");
  }

  // Remove 'let ' prefix
  const rest = line.slice(4);

  // Handle cases with type annotation
  if (rest.includes(':')) {
    // Format: x : TYPE = value or x : TYPE = valueTYPE
    const [left, right] = rest.split('=');
    const leftParts = left.split(':');
    const varName = leftParts[0].trim();
    const declaredType = leftParts[1].trim();
    let value = right.trim();

    // Check for value with type suffix (e.g., 0U8)
    let valueType = null;
    for (const t of Object.keys(typeMap)) {
      if (value.endsWith(t)) {
        valueType = t;
        value = value.slice(0, value.length - t.length);
        value = value.trim();
        break;
      }
    }
    if (valueType) {
      if (declaredType !== valueType) {
        throw new Error('Type mismatch between declared and literal type');
      }
    }
    if (!typeMap[declaredType]) {
      throw new Error("Unsupported type.");
    }
    return `${typeMap[declaredType]} ${varName} = ${value};`;
  }

  // Handle cases without type annotation
  // Format: x = value or x = valueTYPE
  const eqIdx = rest.indexOf('=');
  if (eqIdx === -1) {
    throw new Error("Unsupported input format.");
  }
  const varName = rest.slice(0, eqIdx).trim();
  let value = rest.slice(eqIdx + 1).trim();
  let type = null;
  for (const t of Object.keys(typeMap)) {
    if (value.endsWith(t)) {
      type = t;
      value = value.slice(0, value.length - t.length);
      value = value.trim();
      break;
    }
  }
  if (type) {
    return `${typeMap[type]} ${varName} = ${value};`;
  }
  // Default to int32_t
  return `int32_t ${varName} = ${value};`;
}

module.exports = compile;
