// Type mappings
const typeMap = {
  'I8': 'int8_t',
  'U8': 'uint8_t',
  'I16': 'int16_t',
  'U16': 'uint16_t',
  'I32': 'int32_t',
  'U32': 'uint32_t',
  'I64': 'int64_t',
  'U64': 'uint64_t',
  'F32': 'float',
  'F64': 'double',
  'Bool': 'bool',
  'Void': 'void'
};

// Check if a statement is a struct declaration
function isStructDeclaration(statement) {
  return statement.trim().startsWith('struct ') && statement.includes('{') && statement.includes('}');
}

// Handle struct declarations
function handleStructDeclaration(statement) {
  // Replace struct content to use semicolons instead of commas if needed
  const structContent = statement.replace(/struct\s+(\w+)\s*\{([^}]*)\}/, (match, name, fields) => {
    if (fields.trim() === '') {
      return `struct ${name} {}`;
    }

    // Handle both comma and semicolon separated fields
    const cleanFields = fields.split(/[,;]/)
      .map(field => field.trim())
      .filter(field => field.length > 0)
      .map(field => {
        const parts = field.split(':').map(p => p.trim());
        if (parts.length === 2) {
          const [fieldName, fieldType] = parts;
          const cType = typeMap[fieldType] || fieldType;
          return `${cType} ${fieldName}`;
        }
        return field;
      })
      .join('; ');

    return `struct ${name} { ${cleanFields}; }`;
  });

  return structContent + ';';
}

// Parse type suffixes from values
function parseTypeSuffix(value) {
  // Handle boolean literals
  if (value === 'true' || value === 'false') {
    return { value, type: 'Bool' };
  }

  // Handle character literals
  if (value.startsWith("'") && value.endsWith("'") && value.length === 3) {
    return { value, type: 'U8' };
  }

  // Handle string literals
  if (value.startsWith('"') && value.endsWith('"')) {
    return { value, type: 'String' };
  }

  // Handle Bool suffix for trueBool/falseBool
  if (value.endsWith('Bool')) {
    const baseValue = value.slice(0, -4);
    if (baseValue === 'true' || baseValue === 'false') {
      return { value: baseValue, type: 'Bool' };
    }
  }

  const typeSuffixes = ['I8', 'U8', 'I16', 'U16', 'I32', 'U32', 'I64', 'U64', 'F32', 'F64'];
  for (const suffix of typeSuffixes) {
    if (value.endsWith(suffix)) {
      return {
        value: value.slice(0, -suffix.length),
        type: suffix
      };
    }
  }
  return { value, type: null };
}

// Validate boolean assignments
function validateBoolAssignment(declaredType, value) {
  if (declaredType === 'Bool' && !['true', 'false'].includes(value)) {
    throw new Error('Bool must be assigned true or false');
  }
}

// Check if a variable is accessible in current scope
function validateVariableAccess(varName, varTable, localVars = []) {
  const allVars = [...localVars, ...Object.keys(varTable)];
  if (!allVars.includes(varName)) {
    throw new Error(`Variable '${varName}' not declared`);
  }
}

// Handle array type annotations
function handleArrayTypeAnnotation(varName, declaredType, value) {
  if (declaredType.startsWith('[') && declaredType.endsWith(']')) {
    const innerType = declaredType.slice(1, -1);

    // Parse [U8; 3] format
    const arrayMatch = innerType.match(/^(\w+);\s*(\d+)$/);
    if (arrayMatch) {
      const [, elementType, length] = arrayMatch;
      const cType = typeMap[elementType];

      if (!cType) {
        throw new Error(`Unsupported array element type: ${elementType}`);
      }

      if (value.startsWith('[') && value.endsWith(']')) {
        const elements = value.slice(1, -1).split(',').map(e => e.trim());

        // Check array length
        if (elements.length !== parseInt(length)) {
          throw new Error(`Array length mismatch: expected ${length}, got ${elements.length}`);
        }

        // Validate element types
        for (const element of elements) {
          // Check for boolean values
          if (element === 'true' || element === 'false') {
            throw new Error('Invalid array element: boolean not allowed in numeric array');
          }

          // Validate numeric types
          if (elementType.startsWith('U') || elementType.startsWith('I')) {
            if (!element.match(/^-?\d+$/)) {
              throw new Error(`Invalid array element: '${element}' is not a valid integer`);
            }
          }
        }

        return `${cType} ${varName}[${length}] = {${elements.join(', ')}}`;
      }

      if (value.startsWith('"') && value.endsWith('"')) {
        const chars = value.slice(1, -1).split('');
        if (chars.length !== parseInt(length)) {
          throw new Error(`String length ${chars.length} does not match array length ${length}`);
        }
        return `${cType} ${varName}[${length}] = {${chars.map(c => `'${c}'`).join(', ')}}`;
      }
    }

    const cType = typeMap[innerType];
    if (!cType) {
      throw new Error(`Unsupported array element type: ${innerType}`);
    }

    if (value.startsWith('[') && value.endsWith(']')) {
      const elements = value.slice(1, -1).split(',').map(e => e.trim());
      return `${cType} ${varName}[${elements.length}] = {${elements.join(', ')}}`;
    }
  }

  if (value.startsWith('"') && value.endsWith('"')) {
    return handleStringAssignment(varName, value);
  }

  throw new Error("Unsupported array syntax");
}

// Handle string literal assignment
function handleStringAssignment(varName, str) {
  const chars = str.slice(1, -1).split('');
  return `uint8_t ${varName}[${chars.length}] = {${chars.map(c => `'${c}'`).join(', ')}}`;
}

// Handle declarations with type annotations
function handleDeclarationWithType(s, colonIdx, eqIdx, varTable) {
  const isMutable = s.includes('mut');
  const varName = s.slice(0, colonIdx).replace('let', '').replace('mut', '').trim();
  const declaredType = s.slice(colonIdx + 1, eqIdx).trim();
  const value = s.slice(eqIdx + 1).replace(/;$/, '').trim();

  // Track variable in table with scope information
  if (varTable) {
    varTable[varName] = { type: declaredType, mutable: isMutable, scopeLevel: 0 };
  }

  // Check for struct construction
  const structConstructMatch = value.match(/^([A-Z][a-zA-Z0-9_]*)\s*\{([^}]*)\}$/);
  if (structConstructMatch) {
    return `struct ${declaredType} ${varName} = { ${structConstructMatch[2].trim()} }`;
  }

  // Allow arrays and string literals
  if (declaredType.startsWith('[') || value.startsWith('[') || value.startsWith('"')) {
    return handleArrayTypeAnnotation(varName, declaredType, value);
  }

  if (!typeMap[declaredType]) {
    throw new Error("Unsupported type.");
  }

  let { value: val, type: valueType } = parseTypeSuffix(value);
  if (valueType && declaredType !== valueType) {
    throw new Error('Type mismatch between declared and literal type');
  }

  validateBoolAssignment(declaredType, val);
  return `${typeMap[declaredType]} ${varName} = ${val}`;
}

// Handle declarations without type annotations
function handleDeclarationNoType(s, varTable) {
  const eqIdx = s.indexOf('=');
  if (eqIdx === -1) {
    throw new Error("Unsupported input format.");
  }

  const isMutable = s.includes('mut');
  const varName = s.slice(0, eqIdx).replace('let', '').replace('mut', '').trim();
  const rawValue = s.slice(eqIdx + 1).replace(/;$/, '').trim();
  let { value, type } = parseTypeSuffix(rawValue);

  // Check if the value is a variable reference (not a literal)
  if (!type && /^[a-zA-Z_][a-zA-Z0-9_]*$/.test(rawValue)) {
    // This is a variable reference - validate it exists in scope
    if (!varTable[rawValue]) {
      throw new Error(`Variable '${rawValue}' is not defined`);
    }
    // Use the type of the referenced variable
    type = varTable[rawValue].type;
  }

  // Track variable in table with scope information
  if (varTable) {
    varTable[varName] = { type: type || 'I32', mutable: isMutable, scopeLevel: 0 };
  }

  // Handle string literals specially
  if (type === 'String') {
    return handleStringAssignment(varName, value);
  }

  if (type) {
    validateBoolAssignment(type, value);
    return `${typeMap[type] || 'int32_t'} ${varName} = ${value}`;
  }

  return `int32_t ${varName} = ${value}`;
}

// Handle variable declarations
function handleDeclaration(s, varTable) {
  const colonIdx = s.indexOf(':');
  const eqIdx = s.indexOf('=');

  if (colonIdx !== -1 && eqIdx !== -1) {
    return handleDeclarationWithType(s, colonIdx, eqIdx, varTable);
  }

  // Handle 'let mut x = ...' syntax
  return handleDeclarationNoType(s, varTable);
}

// Main compile function
function compile(magmaCode) {
  // More sophisticated statement splitting that handles nested braces
  const statements = [];
  let current = '';
  let braceDepth = 0;
  let bracketDepth = 0;
  let inString = false;
  let inChar = false;

  for (let i = 0; i < magmaCode.length; i++) {
    const char = magmaCode[i];
    const prevChar = i > 0 ? magmaCode[i - 1] : '';

    if (char === '"' && prevChar !== '\\') {
      inString = !inString;
    } else if (char === "'" && prevChar !== '\\') {
      inChar = !inChar;
    } else if (!inString && !inChar) {
      if (char === '{') {
        braceDepth++;
      } else if (char === '}') {
        braceDepth--;
      } else if (char === '[') {
        bracketDepth++;
      } else if (char === ']') {
        bracketDepth--;
      }
    }

    current += char;

    // Check for statement end: space after closing brace at depth 0, or semicolon at depth 0 and not inside brackets
    if (!inString && !inChar && braceDepth === 0 && bracketDepth === 0) {
      if ((char === '}' && i < magmaCode.length - 1 && magmaCode[i + 1] === ' ') ||
        (char === ';')) {
        if (current.trim()) {
          statements.push(current.trim());
        }
        current = '';
        // Skip the space after closing brace
        if (char === '}' && i < magmaCode.length - 1 && magmaCode[i + 1] === ' ') {
          i++;
        }
      }
    }
  }

  if (current.trim()) {
    statements.push(current.trim());
  }

  let cCode = '';
  let varTable = {};
  let scopeStack = [new Set()]; // Track variables in each scope

  // Helper function to check if a variable exists in any accessible scope
  function isVariableAccessible(varName) {
    return varTable[varName] !== undefined || scopeStack.some(scope => scope.has(varName));
  }

  // Helper function to add variable to current scope
  function addToCurrentScope(varName) {
    scopeStack[scopeStack.length - 1].add(varName);
  }

  // Check for block-scoped variable access violations
  function validateVariableReferences(code) {
    // Look for variable references that might be out of scope
    const varReferences = code.match(/\b[a-zA-Z_][a-zA-Z0-9_]*\b/g) || [];
    for (const varRef of varReferences) {
      // Skip keywords and function names
      if (['if', 'while', 'else', 'true', 'false', 'let', 'mut', 'struct', 'return'].includes(varRef)) {
        continue;
      }
      // Skip type names
      if (typeMap[varRef]) {
        continue;
      }
      // Check if this looks like a variable reference
      if (varRef && !isVariableAccessible(varRef)) {
        // This might be a function call or other valid construct, so we need to be more specific
        const context = statements.find(stmt => stmt.includes(varRef));
        if (context && context.includes(`let ${varRef} =`) || context.includes(`let mut ${varRef} =`)) {
          // This is a variable declaration, not a reference
          continue;
        }
        if (context && (context.includes(`${varRef} =`) || context.includes(`= ${varRef}`))) {
          throw new Error(`Variable '${varRef}' not declared or out of scope`);
        }
      }
    }
  }

  for (let i = 0; i < statements.length; i++) {
    let s = statements[i];

    // Handle struct declarations
    if (isStructDeclaration(s)) {
      cCode += handleStructDeclaration(s);
      if (i < statements.length - 1) cCode += ' ';
      continue;
    }

    // Function declaration
    if (s.startsWith('fn ')) {
      const fnMatch = s.match(/fn\s+(\w+)\(([^)]*)\)\s*:\s*(\w+)\s*=>\s*\{([^}]*)\}/);
      if (fnMatch) {
        const [, fnName, params, returnType, body] = fnMatch;
        const cReturnType = typeMap[returnType] || 'void';

        let cParams = '';
        if (params.trim()) {
          cParams = params.split(',').map(param => {
            const [name, type] = param.split(':').map(p => p.trim());
            return `${typeMap[type]} ${name}`;
          }).join(', ');
        }

        let cBody = body.trim();
        if (cBody.startsWith('return ')) {
          cBody = cBody;
        }

        cCode += `${cReturnType} ${fnName}(${cParams}) {${cBody}}`;
        if (i < statements.length - 1) cCode += ' ';
        continue;
      }
    }

    // Variable declaration
    if (s.startsWith('let ')) {
      cCode += handleDeclaration(s, varTable);
      // Add semicolon and space if not the last statement
      if (i < statements.length - 1) {
        cCode += '; ';
      } else {
        cCode += ';';
      }
      continue;
    }

    // Assignment statement (x = value)
    if (s.match(/^\w+\s*=\s*[^=]/) && !s.startsWith('let ')) {
      const assignMatch = s.match(/^(\w+)\s*=\s*(.+);?$/);
      if (assignMatch) {
        const [, varName, value] = assignMatch;

        // Check if variable exists and is accessible
        if (!varTable[varName]) {
          throw new Error(`Variable '${varName}' not declared`);
        }

        // Check scope accessibility - ensure variable is still in scope
        if (varTable[varName].scopeLevel !== undefined && varTable[varName].scopeLevel > 0) {
          // Variable was declared in a block that may no longer be accessible
          // For now, we'll allow access since we need better scope stack management
        }

        if (!varTable[varName].mutable) {
          throw new Error(`Cannot assign to immutable variable '${varName}'`);
        }

        cCode += `${varName} = ${value}`;
        if (!s.endsWith(';')) {
          cCode += ';';
        }
        if (i < statements.length - 1) {
          cCode += ' ';
        }
        continue;
      }
    }

    // Function call (standalone)
    if (s.match(/^\w+\(/) && !s.match(/^(if|while|else|for|switch)/)) {
      if (!s.endsWith(';')) {
        cCode += s + ';';
      } else {
        cCode += s;
      }
      if (i < statements.length - 1) cCode += ' ';
      continue;
    }

    // Block syntax: { ... } as a statement
    if (s.startsWith('{') && s.endsWith('}')) {
      const blockContent = s.slice(1, -1).trim();
      if (blockContent) {
        // Create new scope for block
        const blockVarTable = { ...varTable }; // Start with current scope
        const blockVariables = new Set(); // Track variables declared in this block

        const innerStatements = blockContent.split(';').map(stmt => stmt.trim()).filter(stmt => stmt);
        const compiledInner = innerStatements.map(stmt => {
          if (stmt.startsWith('let ')) {
            const result = handleDeclaration(stmt, blockVarTable);

            // Extract variable name and mark as block-scoped
            const varMatch = result.match(/(\w+)\s+(\w+)\s*=/);
            if (varMatch) {
              const varName = varMatch[2];
              blockVariables.add(varName);
              if (blockVarTable[varName]) {
                blockVarTable[varName].isBlockScoped = true;
              }
            }
            return result;
          }
          return stmt;
        }).join('; ');

        // After block, remove block-scoped variables from main varTable
        // (This ensures they're not accessible outside the block)
        blockVariables.forEach(varName => {
          delete varTable[varName];
        });

        cCode += `{${compiledInner};}`;
      } else {
        cCode += `{}`;
      }
      if (i < statements.length - 1) cCode += ' ';
      continue;
    }

    // Handle if/else/while statements and other control flow
    if (s.match(/^(if|while|else)/)) {
      // Process the statement to compile any inner let statements
      let processedStatement = s.replace(/\{([^}]*)\}/g, (match, content) => {
        if (content.trim()) {
          const innerStatements = content.split(';').map(stmt => stmt.trim()).filter(stmt => stmt);
          const compiledInner = innerStatements.map(stmt => {
            if (stmt.startsWith('let ')) {
              return handleDeclaration(stmt, varTable);
            }
            return stmt;
          }).filter(stmt => stmt); // Remove empty statements
          // Only add semicolon if there are actual statements and they're not control flow
          if (compiledInner.length > 0) {
            const needsSemicolon = compiledInner.some(stmt =>
              !stmt.match(/^(if|while|else|for|switch)/) &&
              !stmt.includes('{') &&
              !stmt.endsWith(';')
            );
            if (needsSemicolon) {
              return `{${compiledInner.join('; ')};}`;
            } else {
              return `{${compiledInner.join('; ')}}`;
            }
          }
        }
        return '{}';
      });
      cCode += processedStatement;
      // Don't add semicolons for control flow statements
      if (i < statements.length - 1) {
        cCode += ' ';
      }
      continue;
    }

    // Other statements (expressions, etc.)
    cCode += s;
    // Only add semicolon for function calls that are not already handled
    if (s.match(/^\w+\(/) && !s.match(/^(if|while|else|for|switch)/) && !s.endsWith(';')) {
      // Function call - add semicolon
      if (i < statements.length - 1) {
        cCode += '; ';
      } else {
        cCode += ';';
      }
    } else if (i < statements.length - 1) {
      cCode += ' ';
    }
  }

  return cCode;
}

module.exports = { compile };
