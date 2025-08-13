
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
  // Handle single-quoted character as U8
  if (/^'.'$/.test(value)) {
    return { value: value, type: 'U8' };
  }
  return { value: value, type: null };
}

function handleArrayTypeAnnotation(varName, declaredType, right) {
  function validateArrayElements(elems) {
    return elems.every(e => {
      if (e.length === 0) return false;
      if (e[0] === '-' && e.length > 1) {
        return e.slice(1).split('').every(ch => ch >= '0' && ch <= '9');
      }
      return e.split('').every(ch => ch >= '0' && ch <= '9');
    });
  }
  function parseArrayValue(arrVal, arrLen) {
    if (arrVal.startsWith('"') && arrVal.endsWith('"')) {
      const chars = arrVal.slice(1, -1).split('');
      if (chars.length !== arrLen) {
        throw new Error("String length does not match array length.");
      }
      return chars.map(c => `'${c}'`);
    } else {
      if (!arrVal.startsWith('[') || !arrVal.endsWith(']')) {
        throw new Error("Array value must be in brackets.");
      }
      const elemsStr = arrVal.slice(1, -1);
      const elems = elemsStr.split(',').map(e => e.trim()).filter(e => e.length > 0);
      if (elems.length !== arrLen) {
        throw new Error("Array length does not match type annotation.");
      }
      if (!validateArrayElements(elems)) {
        throw new Error("Array elements must be integers.");
      }
      return elems;
    }
  }
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
  const arrVal = right.trim();
  const elems = parseArrayValue(arrVal, arrLen);
  return `${typeMap[elemType]} ${varName}[${arrLen}] = {${elems.join(', ')}};`
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
  // Normalize [U8; 3, 3] to [[U8; 3]; 3] for multi-dimensional shorthand
  if (declaredType.startsWith('[') && declaredType.endsWith(']') && declaredType.includes(';')) {
    // Manual parse for multi-dimensional shorthand: [U8; 3, 3]
    const inner = declaredType.slice(1, -1);
    const semiIdx = inner.indexOf(';');
    if (semiIdx !== -1) {
      const baseType = inner.slice(0, semiIdx).trim();
      const dimsStr = inner.slice(semiIdx + 1).trim();
      const dims = dimsStr.split(',').map(d => d.trim()).filter(d => d.length > 0);
      if (dims.length > 1) {
        // Build nested type string
        let nestedType = baseType;
        for (let i = dims.length - 1; i >= 0; i--) {
          nestedType = `[${nestedType}; ${dims[i]}]`;
        }
        return handleArrayTypeAnnotation(varName, nestedType, right);
      }
    }
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
  // Handle string literal assignment: let x = "abc";
  function handleStringAssignment(varName, str) {
    const chars = str.slice(1, -1).split('');
    return `uint8_t ${varName}[${chars.length}] = {${chars.map(c => `'${c}'`).join(', ')}};`;
  }
  if (input.trim().startsWith('{') && input.trim().endsWith('}')) {
    const inner = input.trim().slice(1, -1).trim();
    if (inner.length === 0) return '{}';
    // Compile block contents with a fresh variable table
    // (do not use outer varTable)
    function compileBlock(blockInput) {
      const statements = smartSplit(blockInput);
      const varTable = {};
      const results = [];
      for (const stmt of statements) {
        results.push(handleStatement(stmt.trim()));
      }
      return `{${results.map(r => r.endsWith(';') ? r : r + ';').join(' ')}}`;
    }
    return compileBlock(inner);
  }
  if (input.trim() === '{}') {
    return '{}';
  }
  // Block syntax: { ... } as a statement
  function isBlock(s) {
    return s.startsWith('{') && s.endsWith('}');
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
}
const statements = smartSplit(input);
const varTable = {};
const results = [];
function handleBlock(s) {
  const inner = s.slice(1, -1).trim();
  if (inner.length === 0) {
    return '{}';
  } else {
    const compiledInner = compile(inner);
    const blockContent = compiledInner.endsWith(';') ? compiledInner.slice(0, -1) : compiledInner;
    return `{${blockContent}}`;
  }
}
function handleDeclaration(s, varTable) {
  s = s.slice(4).trim();
  let isMut = false;
  if (s.startsWith('mut ')) {
    isMut = true;
    s = s.slice(4).trim();
  }
  let varName;
  if (s.includes(':')) {
    const [left, right] = s.split('=');
    varName = left.split(':')[0].trim();
    varTable[varName] = { mut: isMut };
    return handleTypeAnnotation(s);
  } else {
    const eqIdx = s.indexOf('=');
    varName = s.slice(0, eqIdx).trim();
    varTable[varName] = { mut: isMut };
    const value = s.slice(eqIdx + 1).trim();
    if (value.startsWith('"') && value.endsWith('"')) {
      return handleStringAssignment(varName, value);
    } else {
      return handleNoTypeAnnotation(s);
    }
  }
}
function handleAssignment(s, varTable) {
  const eqIdx = s.indexOf('=');
  const varName = s.slice(0, eqIdx).trim();
  if (!varTable[varName]) {
    throw new Error(`Variable '${varName}' not declared`);
  }
  if (!varTable[varName].mut) {
    throw new Error(`Cannot assign to immutable variable '${varName}'`);
  }
  return `${varName} = ${s.slice(eqIdx + 1).trim()};`;
}
function isAssignment(s) {
  return /^[a-zA-Z_][a-zA-Z0-9_]*\s*=/.test(s);
}
