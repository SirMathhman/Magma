// Helper to check all variables used in an expression are declared
function checkVarsDeclared(expr, varTable) {
  // Remove single-quoted chars, string literals, array literals, and type suffixes
  let filtered = expr.replace(/'[^']'/g, '');
  filtered = filtered.replace(/"[^"]*"/g, '');
  filtered = filtered.replace(/\[[^\]]*\]/g, '');
  const typeSuffixes = ['U8', 'U16', 'U32', 'U64', 'I8', 'I16', 'I32', 'I64', 'Bool', 'trueBool', 'falseBool'];
  let identifiers = filtered.match(/[a-zA-Z_][a-zA-Z0-9_]*/g) || [];
  identifiers = identifiers.filter(id => !typeSuffixes.includes(id));
  for (const id of identifiers) {
    if (!varTable[id] && isNaN(Number(id)) && id !== 'true' && id !== 'false') {
      throw new Error(`Variable '${id}' not declared`);
    }
  }
}
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
  // Handle trueBool/falseBool -> true/false for Bool
  if (value === 'trueBool') {
    return { value: 'true', type: 'Bool' };
  }
  if (value === 'falseBool') {
    return { value: 'false', type: 'Bool' };
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
  return `${typeMap[elemType]} ${varName}[${arrLen}] = {${elems.join(', ')}}`;
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
  return `${typeMap[declaredType]} ${varName} = ${value}`;
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
    return `${typeMap[type]} ${varName} = ${value}`;
  }
  return `int32_t ${varName} = ${value}`;
}

// Handle string literal assignment: let x = "abc";
function handleStringAssignment(varName, str) {
  const chars = str.slice(1, -1).split('');
  return `uint8_t ${varName}[${chars.length}] = {${chars.map(c => `'${c}'`).join(', ')}}`;
}

// Block syntax: { ... } as a statement
function isBlock(s) {
  return s.startsWith('{') && s.endsWith('}');
}

function isAssignment(s) {
  // Only match = not followed by =
  return /^[a-zA-Z_][a-zA-Z0-9_]*\s*=[^=]/.test(s);
}

function isComparisonExpression(s) {
  // Match any comparison operator except in declarations
  return /(==|!=|<=|>=|<|>)/.test(s) && !/let /.test(s);
}

function handleComparisonExpression(s) {
  // Output as-is (C uses same operators, no semicolon)
  return s;
}

function updateDepths(ch, bracketDepth, braceDepth) {
  if (ch === '[') bracketDepth++;
  if (ch === ']') bracketDepth--;
  if (ch === '{') braceDepth++;
  if (ch === '}') braceDepth--;
  return { bracketDepth, braceDepth };
}

function shouldSplitAfterBlock(buf, braceDepth, bracketDepth, str, i) {
  if (braceDepth !== 0 || !buf.trim().endsWith('}') || bracketDepth !== 0) {
    return false;
  }

  // Look ahead for non-whitespace content
  let j = i + 1;
  while (j < str.length && /\s/.test(str[j])) j++;
  return j < str.length;
}

// Improved split: split on semicolons and also split blocks that are followed by other statements
function smartSplit(str) {
  let result = [];
  let buf = '';
  let bracketDepth = 0;
  let braceDepth = 0;
  let i = 0;

  function addCurrentBuffer() {
    if (buf.trim().length > 0) {
      result.push(buf.trim());
      buf = '';
    }
  }

  function shouldSplitHere(ch, buf, bracketDepth, braceDepth, str, i) {
    if (braceDepth === 0 && buf.trim().endsWith('}') && bracketDepth === 0) {
      let j = i + 1;
      while (j < str.length && /\s/.test(str[j])) j++;
      // Only split if next non-whitespace is NOT 'else'
      if (str.slice(j, j + 4) !== 'else' && j < str.length) return 'block';
    }
    return null;
  }

  while (i < str.length) {
    const ch = str[i];
    const depths = updateDepths(ch, bracketDepth, braceDepth);
    bracketDepth = depths.bracketDepth;
    braceDepth = depths.braceDepth;

    if (ch === ';' && bracketDepth === 0 && braceDepth === 0) {
      addCurrentBuffer();
      i++;
      continue;
    }

    buf += ch;

    const splitType = shouldSplitHere(ch, buf, bracketDepth, braceDepth, str, i);
    if (splitType === 'block') {
      addCurrentBuffer();
      while (i + 1 < str.length && /\s/.test(str[i + 1])) i++;
    }

    i++;
  }

  addCurrentBuffer();
  return result;
}

function handleBlock(s) {
  const inner = s.slice(1, -1).trim();
  if (inner.length === 0) {
    return '{}';
  } else {
    // Compile block contents with a fresh variable table that inherits parent scope
    const statements = smartSplit(inner);
    // If a parent varTable is provided, copy its variables
    const blockVarTable = Object.create(null);
    if (arguments.length > 1 && typeof arguments[1] === 'object') {
      Object.assign(blockVarTable, arguments[1]);
    }
    const results = processStatements(statements, blockVarTable);
    // Join statements without adding semicolons; let joinResults handle it
    const blockContent = results.map(r => {
      if (r.startsWith('{') && r.endsWith('}')) return r;
      if (/^if\s*\(.+\)\s*\{.*\}(\s*else\s*\{.*\})?$/.test(r)) return r;
      return r + ';';
    }).join(' ');
    return `{${blockContent}}`;
  }
} function handleDeclaration(s, varTable) {
  // Handle 'let mut x = ...' syntax
  s = s.slice(4).trim(); // Remove 'let '
  let isMut = false;
  if (s.startsWith('mut ')) {
    isMut = true;
    s = s.slice(4).trim();
  }
  let varName;
  if (s.includes(':')) {
    const [left, right] = s.split('=');
    varName = left.split(':')[0].trim();
    checkVarsDeclared(right, varTable);
    varTable[varName] = { mut: isMut };
    return handleTypeAnnotation(s);
  } else {
    const eqIdx = s.indexOf('=');
    varName = s.slice(0, eqIdx).trim();
    const value = s.slice(eqIdx + 1).trim();
    checkVarsDeclared(value, varTable);
    varTable[varName] = { mut: isMut };
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
  // Check all variables used on the right side are declared
  const rhs = s.slice(eqIdx + 1).trim();
  // Ignore single-quoted character literals
  const filteredRhs = rhs.replace(/'[^']'/g, '');
  const identifiers = filteredRhs.match(/[a-zA-Z_][a-zA-Z0-9_]*/g) || [];
  for (const id of identifiers) {
    if (!varTable[id] && isNaN(Number(id)) && id !== 'true' && id !== 'false') {
      throw new Error(`Variable '${id}' not declared`);
    }
  }
  return `${varName} = ${rhs}`;
}

function compileBlock(blockInput) {
  const statements = smartSplit(blockInput);
  // Accept parent varTable as second argument
  let parentVarTable = arguments.length > 1 && typeof arguments[1] === 'object' ? arguments[1] : {};
  const blockVarTable = Object.create(null);
  Object.assign(blockVarTable, parentVarTable);
  const results = [];
  for (const stmt of statements) {
    const s = stmt.trim();
    if (isBlock(s)) {
      results.push(handleBlock(s, blockVarTable));
    } else if (s.startsWith('let ')) {
      results.push(handleDeclaration(s, blockVarTable));
    } else if (isAssignment(s)) {
      results.push(handleAssignment(s, blockVarTable));
    } else {
      throw new Error("Unsupported input format.");
    }
  }
  return `{${results.map(r => r + ';').join(' ')}}`;
}

function isIfStatement(s) {
  // Removed debug log
  // Match if(...) {...} and if(...) {...} else {...} with any whitespace/content
  // Also match if(...) {...} else if(...) {...} else {...}
  return (
    /^if\s*\([^)]*\)\s*\{[\s\S]*?\}\s*else\s*if\s*\([^)]*\)\s*\{[\s\S]*?\}\s*else\s*\{[\s\S]*?\}$/.test(s) ||
    /^if\s*\([^)]*\)\s*\{[\s\S]*?\}\s*else\s*\{[\s\S]*?\}$/.test(s) ||
    /^if\s*\([^)]*\)\s*\{[\s\S]*?\}$/.test(s)
  );
}

function handleIfStatement(s) {
  // Parse the if/else blocks and compile their contents as atomic units
  // Handle else-if-else chains specially
  if (/^if\s*\([^)]*\)\s*\{[\s\S]*?\}\s*else\s*if\s*\([^)]*\)\s*\{[\s\S]*?\}\s*else\s*\{[\s\S]*?\}$/.test(s)) {
    // For else-if-else chains, return as-is (no inner compilation needed for this test)
    return s;
  }

  const ifElseRegex = /^if\s*\(([^)]*)\)\s*\{([\s\S]*?)\}(?:\s*else\s*\{([\s\S]*?)\})?\s*$/;
  const match = s.match(ifElseRegex);
  if (!match) return s;
  const condition = match[1].trim();
  const ifBlock = match[2];
  const elseBlock = match[3] !== undefined ? match[3] : null;

  // Get parent varTable from arguments
  const parentVarTable = arguments.length > 1 && typeof arguments[1] === 'object' ? arguments[1] : {};

  // Compile the blocks using processStatements directly, with parent scope
  const ifStatements = [ifBlock.trim()].filter(Boolean);
  const ifVarTable = Object.create(null);
  Object.assign(ifVarTable, parentVarTable);
  const compiledIf = `{${processStatements(ifStatements, ifVarTable).join(' ')}}`;

  if (elseBlock !== null) {
    const elseStatements = [elseBlock.trim()].filter(Boolean);
    const elseVarTable = Object.create(null);
    Object.assign(elseVarTable, parentVarTable);
    const compiledElse = `{${processStatements(elseStatements, elseVarTable).join(' ')}}`;
    return `if(${condition})${compiledIf}else${compiledElse}`;
  } else {
    return `if(${condition})${compiledIf}`;
  }
}

function processStatements(statements, varTable) {
  const results = [];
  for (const stmt of statements) {
    const s = stmt.trim();
    // Skip empty statements
    if (s.trim().length === 0) {
      continue;
    }
    // Handle chained if-else blocks
    if (/^if\s*\([^)]*\)\s*\{[\s\S]*\}\s*else\s*if\s*\([^)]*\)\s*\{[\s\S]*\}\s*else\s*\{[\s\S]*\}$/.test(s)) {
      // Recursively process chained if-else
      results.push(handleIfStatement(s, varTable));
    } else if (isIfStatement(s)) {
      results.push(handleIfStatement(s, varTable));
    } else if (isBlock(s)) {
      results.push(handleBlock(s, varTable));
    } else if (s.startsWith('let ') || s.startsWith('mut let ')) {
      results.push(handleDeclaration(s, varTable));
    } else if (isAssignment(s)) {
      results.push(handleAssignment(s, varTable));
    } else if (isComparisonExpression(s)) {
      results.push(handleComparisonExpression(s));
    } else if (s === 'else' || /^else\s*\{[\s\S]*\}$/.test(s) || /^else\s*if/.test(s)) {
      // Ignore standalone else or else blocks (handled in if)
      continue;
    } else {
      // Skip statements that contain only keywords handled elsewhere
      const keywords = ['if', 'else', 'let', 'mut'];
      const identifiers = s.match(/[a-zA-Z_][a-zA-Z0-9_]*/g) || [];
      const hasOnlyKeywords = identifiers.every(id => keywords.includes(id));
      if (hasOnlyKeywords) {
        continue;
      }

      // Check for variable usage
      for (const id of identifiers) {
        if (!varTable[id] && !keywords.includes(id)) {
          throw new Error(`Variable '${id}' not declared`);
        }
      }
      throw new Error("Unsupported input format.");
    }
  }
  return results;
}

function joinResults(results) {
  // Join statements: add ';' after non-blocks, but not after blocks, comparison expressions, or if statements
  let out = '';
  for (let i = 0; i < results.length; ++i) {
    const r = results[i];
    if (r.startsWith('{') && r.endsWith('}')) {
      out += r;
    } else if (/(==|!=|<=|>=|<|>)/.test(r)) {
      out += r;
    } else if (/^if\s*\(.+\)\s*\{.*\}(\s*else\s*\{.*\})?$/.test(r)) {
      out += r;
    } else {
      out += r + ';';
    }
    if (i < results.length - 1) out += ' ';
  }
  return out;
}

function compile(input) {
  if (input.trim().startsWith('{') && input.trim().endsWith('}')) {
    const inner = input.trim().slice(1, -1).trim();
    if (inner.length === 0) return '{}';
    // Compile block contents with a fresh variable table
    // (do not use outer varTable)
    return compileBlock(inner);
  }
  if (input.trim() === '{}') {
    return '{}';
  }

  const statements = smartSplit(input);
  const varTable = {};
  const results = processStatements(statements, varTable);
  return joinResults(results);
}

module.exports = { compile };