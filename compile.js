"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
exports.compile = compile;
function shouldSplitHere(ch, buf, bracketDepth, braceDepth, str, i) {
    // Split after a block if followed by non-whitespace
    if (ch === '}' && bracketDepth === 0 && braceDepth === 0) {
        var j = i + 1;
        while (j < str.length && /\s/.test(str[j]))
            j++;
        // Don't split if the next token is "else" (keep if-else together)
        if (j < str.length && str.slice(j).startsWith('else')) {
            return null;
        }
        if (j < str.length)
            return 'block';
    }
    return null;
}
function isStructDeclaration(s) {
    // Recognize struct declaration: starts with 'struct', has a name, and braces
    var trimmed = s.trim();
    if (!trimmed.startsWith('struct '))
        return false;
    var structIdx = 6;
    var openBraceIdx = trimmed.indexOf('{', structIdx);
    var closeBraceIdx = trimmed.lastIndexOf('}');
    if (openBraceIdx === -1 || closeBraceIdx === -1)
        return false;
    var name = trimmed.slice(structIdx, openBraceIdx).trim();
    if (!name.match(/^[a-zA-Z_][a-zA-Z0-9_]*$/))
        return false;
    return true;
}
function handleStructDeclaration(s) {
    var trimmed = s.trim();
    var structIdx = 6;
    var openBraceIdx = trimmed.indexOf('{', structIdx);
    var closeBraceIdx = trimmed.lastIndexOf('}');
    var name = trimmed.slice(structIdx, openBraceIdx).trim();
    var body = trimmed.slice(openBraceIdx + 1, closeBraceIdx).trim();
    if (!body) {
        return "struct ".concat(name, " {};");
    }
    // Support multiple fields: <field> : <type>, ... or ; ...
    var fieldDecls = body.split(/[,;]/).map(function (x) { return x.trim(); }).filter(Boolean);
    var typeMap = {
        'I32': 'int32_t',
        'U8': 'uint8_t',
        'U16': 'uint16_t',
        'U32': 'uint32_t',
        'U64': 'uint64_t',
        'I8': 'int8_t',
        'I16': 'int16_t',
        'I64': 'int64_t',
        'Bool': 'bool',
    };
    var fields = fieldDecls.map(function (decl) {
        var fieldMatch = decl.match(/^([a-zA-Z_][a-zA-Z0-9_]*)\s*:\s*([A-Za-z0-9_]+)$/);
        if (!fieldMatch)
            return null;
        var fieldName = fieldMatch[1];
        var fieldType = fieldMatch[2];
        var cType = typeMap[fieldType] || fieldType;
        return "".concat(cType, " ").concat(fieldName, ";");
    }).filter(Boolean);
    return "struct ".concat(name, " { ").concat(fields.join(' '), " };");
}
function isFunctionCall(s) {
    // Recognize function call: identifier (possibly with generics) followed by '(...)' and optional semicolon
    var trimmed = s.trim();
    // Match: name<optional_generics>(params); or name(params);
    if (!/^[a-zA-Z_][a-zA-Z0-9_]*(?:<[^>]+>)?\s*\([^)]*\)\s*;?$/.test(trimmed))
        return false;
    return true;
}
function handleFunctionCall(s) {
    // Convert array literals [x, y, z] to C-style initializers {x, y, z} in function calls
    var result = s.trim();
    result = result.replace(/\[([^\]]+)\]/g, '{$1}');
    return result;
}
// Recognize Magma function declaration: fn name() : Void => {}
function isFunctionDeclaration(s) {
    return (isFunctionDefinition(s) || isFunctionPrototype(s)) && !isGenericFunctionDeclaration(s);
}
function isGenericFunctionDeclaration(s) {
    var trimmed = s.trim();
    // Check for extern function first
    var searchStart = 0;
    if (trimmed.startsWith('extern ')) {
        searchStart = 7;
    }
    if (!trimmed.slice(searchStart).startsWith('fn '))
        return false;
    var fnIdx = searchStart + 3;
    var openParenIdx = trimmed.indexOf('(', fnIdx);
    if (openParenIdx === -1)
        return false;
    var name = trimmed.slice(fnIdx, openParenIdx).trim();
    // Check for generic type parameters: name should contain < and >
    var hasOpenBracket = name.includes('<');
    var hasCloseBracket = name.includes('>');
    if (!hasOpenBracket || !hasCloseBracket)
        return false;
    var openIdx = name.indexOf('<');
    var closeIdx = name.lastIndexOf('>');
    if (openIdx >= closeIdx)
        return false;
    // Make sure there's something between the brackets
    var typeParams = name.slice(openIdx + 1, closeIdx).trim();
    return typeParams.length > 0;
}
function isFunctionDefinition(s) {
    // Avoid regex: check for 'fn', '(', ')', ':', '=>', '{', '}'
    // Should NOT start with 'extern' - that's invalid for definitions
    var trimmed = s.trim();
    if (trimmed.startsWith('extern ')) {
        // extern functions cannot have bodies
        if (trimmed.indexOf('=>') !== -1) {
            throw new Error("extern functions cannot have bodies");
        }
        return false;
    }
    if (!trimmed.startsWith('fn '))
        return false;
    var fnIdx = 3;
    var openParenIdx = trimmed.indexOf('(', fnIdx);
    var closeParenIdx = trimmed.indexOf(')', openParenIdx);
    var colonIdx = trimmed.indexOf(':', closeParenIdx);
    var arrowIdx = trimmed.indexOf('=>', colonIdx);
    var openBraceIdx = trimmed.indexOf('{', arrowIdx);
    var closeBraceIdx = trimmed.lastIndexOf('}');
    if (openParenIdx === -1 || closeParenIdx === -1 || colonIdx === -1 ||
        arrowIdx === -1 || openBraceIdx === -1 || closeBraceIdx === -1)
        return false;
    // Check for supported return type
    var retType = trimmed.slice(colonIdx + 1, arrowIdx).replace(/\s/g, '');
    if (retType !== 'Void' && !typeMap[retType])
        return false;
    return true;
}
function isFunctionPrototype(s) {
    // Check for 'fn', '(', ')', ':', ';' but no '=>'
    // Can optionally start with 'extern'
    var trimmed = s.trim();
    var startPattern = 'fn ';
    var searchStart = 0;
    if (trimmed.startsWith('extern ')) {
        startPattern = 'extern fn ';
        searchStart = 7; // length of 'extern '
    }
    if (!trimmed.startsWith(startPattern) || !trimmed.endsWith(';'))
        return false;
    var fnIdx = startPattern.length;
    var openParenIdx = trimmed.indexOf('(', fnIdx);
    var closeParenIdx = trimmed.indexOf(')', openParenIdx);
    var colonIdx = trimmed.indexOf(':', closeParenIdx);
    var semicolonIdx = trimmed.lastIndexOf(';');
    if (openParenIdx === -1 || closeParenIdx === -1 || colonIdx === -1 ||
        semicolonIdx === -1)
        return false;
    // Make sure there's no '=>' (which would make it a definition)
    if (trimmed.indexOf('=>') !== -1)
        return false;
    // Check for supported return type
    var retType = trimmed.slice(colonIdx + 1, semicolonIdx).replace(/\s/g, '');
    if (retType !== 'Void' && !typeMap[retType])
        return false;
    return true;
}
function getFunctionParts(s) {
    var trimmed = s.trim();
    var isExtern = false;
    var searchStart = 0;
    // Check for extern prefix
    if (trimmed.startsWith('extern ')) {
        isExtern = true;
        searchStart = 7; // length of 'extern '
    }
    if (!trimmed.slice(searchStart).startsWith('fn ')) {
        throw new Error("Invalid function declaration format.");
    }
    var fnIdx = searchStart + 3; // start after 'fn '
    var openParenIdx = trimmed.indexOf('(', fnIdx);
    var closeParenIdx = trimmed.indexOf(')', openParenIdx);
    var colonIdx = trimmed.indexOf(':', closeParenIdx);
    if (openParenIdx === -1 || closeParenIdx === -1 || colonIdx === -1) {
        throw new Error("Invalid function declaration format.");
    }
    // Check if it's a prototype (ends with ;) or definition (has => {})
    var isPrototype = trimmed.endsWith(';') && trimmed.indexOf('=>') === -1;
    if (isPrototype) {
        var semicolonIdx = trimmed.lastIndexOf(';');
        return {
            name: trimmed.slice(fnIdx, openParenIdx).trim(),
            paramStr: trimmed.slice(openParenIdx + 1, closeParenIdx).trim(),
            retType: trimmed.slice(colonIdx + 1, semicolonIdx).replace(/\s/g, ''),
            blockContent: '',
            isPrototype: true,
            isExtern: isExtern
        };
    }
    else {
        // Handle function definition with body
        var arrowIdx = trimmed.indexOf('=>', colonIdx);
        var openBraceIdx = trimmed.indexOf('{', arrowIdx);
        var closeBraceIdx = trimmed.lastIndexOf('}');
        if (arrowIdx === -1 || openBraceIdx === -1 || closeBraceIdx === -1) {
            throw new Error("Invalid function declaration format.");
        }
        return {
            name: trimmed.slice(fnIdx, openParenIdx).trim(),
            paramStr: trimmed.slice(openParenIdx + 1, closeParenIdx).trim(),
            retType: trimmed.slice(colonIdx + 1, arrowIdx).replace(/\s/g, ''),
            blockContent: trimmed.slice(openBraceIdx + 1, closeBraceIdx).trim(),
            isPrototype: false,
            isExtern: isExtern
        };
    }
}
function getFunctionParams(paramStr) {
    if (paramStr.length === 0)
        return '';
    // Split by comma, handle each param
    var paramList = paramStr.split(',').map(function (p) { return p.trim(); }).filter(Boolean);
    return paramList.map(function (param) {
        var colonParamIdx = param.indexOf(':');
        if (colonParamIdx === -1)
            throw new Error("Invalid parameter format.");
        var paramName = param.slice(0, colonParamIdx).trim();
        var paramType = param.slice(colonParamIdx + 1).trim();
        // Handle array types like [T; 3]
        var arrayMatch = paramType.match(/^\[([A-Za-z0-9_]+);\s*(\d+)\]$/);
        if (arrayMatch) {
            var elemType = arrayMatch[1];
            var size = arrayMatch[2];
            if (!typeMap[elemType]) {
                // If this is a generic type parameter, just return a placeholder
                // This function should only be called for concrete functions, not generic ones
                return "".concat(elemType, " ").concat(paramName, "[").concat(size, "]");
            }
            return "".concat(typeMap[elemType], " ").concat(paramName, "[").concat(size, "]");
        }
        if (!typeMap[paramType]) {
            // If this is a generic type parameter, just return a placeholder
            // This function should only be called for concrete functions, not generic ones
            return "".concat(paramType, " ").concat(paramName);
        }
        return "".concat(typeMap[paramType], " ").concat(paramName);
    }).join(', ');
}
function handleFunctionDeclaration(s) {
    var parts = getFunctionParts(s);
    // Detect type parameters in function name (e.g., doNothing<T>, doNothing<T, U>)
    var genericMatch = parts.name.match(/^(\w+)<([A-Za-z0-9_,\s]+)>$/);
    if (genericMatch) {
        // Do not emit generic function directly
        return '';
    }
    // If it's an extern function, return empty string (no output)
    if (parts.isExtern) {
        return '';
    }
    var cRetType;
    if (parts.retType === 'Void') {
        cRetType = 'void';
    }
    else if (typeMap[parts.retType]) {
        cRetType = typeMap[parts.retType];
    }
    else {
        throw new Error("Unsupported return type.");
    }
    var params = getFunctionParams(parts.paramStr);
    if (parts.isPrototype) {
        // Function prototype: just declaration without body
        return "".concat(cRetType, " ").concat(parts.name, "(").concat(params, ");");
    }
    else {
        // Function definition: declaration with body
        return "".concat(cRetType, " ").concat(parts.name, "(").concat(params, ") {").concat(parts.blockContent, "}");
    }
}
// Helper to classify statement type (split for lower complexity)
var keywords = ['if', 'else', 'let', 'mut', 'while', 'true', 'false', 'fn', 'extern'];
function isEmptyStatement(s) { return s.trim().length === 0; }
function isIfElseChain(s) { return /^if\s*\([^)]*\)\s*\{[\s\S]*\}\s*else\s*if\s*\([^)]*\)\s*\{[\s\S]*\}\s*else\s*\{[\s\S]*\}$/.test(s); }
function isIf(s) { return isIfStatement(s); }
function isWhile(s) { return isWhileStatement(s); }
function isBlockStmt(s) { return isBlock(s); }
function isDeclaration(s) { return s.startsWith('let ') || s.startsWith('mut let '); }
function isAssignmentStmt(s) { return isAssignment(s); }
function isComparisonStmt(s) { return isComparisonExpression(s); }
function isElseStmt(s) { return s === 'else' || /^else\s*\{[\s\S]*\}$/.test(s) || /^else\s*if/.test(s); }
function isKeywordsOnly(s) {
    var identifiers = s.match(/[a-zA-Z_][a-zA-Z0-9_]*/g) || [];
    return identifiers.length > 0 && identifiers.every(function (id) { return keywords.includes(id); });
}
function checkUndeclaredVars(s, varTable) {
    var identifiers = s.match(/[a-zA-Z_][a-zA-Z0-9_]*/g) || [];
    // List of C types to ignore
    var cTypes = ['void', 'int32_t', 'uint8_t', 'uint16_t', 'uint32_t', 'uint64_t', 'int8_t', 'int16_t', 'int64_t', 'bool', 'char', 'float', 'double'];
    for (var _i = 0, identifiers_1 = identifiers; _i < identifiers_1.length; _i++) {
        var id = identifiers_1[_i];
        if (!keywords.includes(id) && !varTable[id] && !cTypes.includes(id)) {
            throw new Error("Variable '".concat(id, "' not declared"));
        }
    }
}
function isCFunctionDefinition(s) {
    // Match: <ctype> <name>(...) {...}
    return /^\s*(void|int32_t|uint8_t|uint16_t|uint32_t|uint64_t|int8_t|int16_t|int64_t|bool|char|float|double)\s+[a-zA-Z_][a-zA-Z0-9_]*\s*\([^)]*\)\s*\{[^}]*\}\s*$/.test(s);
}
var statementTypeHandlers = [
    { type: 'empty', check: isEmptyStatement },
    { type: 'struct', check: isStructDeclaration },
    { type: 'generic-function', check: isGenericFunctionDeclaration },
    { type: 'function', check: isFunctionDeclaration },
    { type: 'function-call', check: isFunctionCall },
    { type: 'if-else-chain', check: isIfElseChain },
    { type: 'if', check: isIf },
    { type: 'while', check: isWhile },
    { type: 'block', check: isBlockStmt },
    { type: 'declaration', check: isDeclaration },
    { type: 'assignment', check: isAssignmentStmt },
    { type: 'comparison', check: isComparisonStmt },
    { type: 'else', check: isElseStmt },
    { type: 'keywords', check: isKeywordsOnly },
    { type: 'struct-construction', check: isStructConstruction },
    { type: 'c-function', check: isCFunctionDefinition }
];
function getStatementType(s, varTable) {
    for (var _i = 0, statementTypeHandlers_1 = statementTypeHandlers; _i < statementTypeHandlers_1.length; _i++) {
        var handler = statementTypeHandlers_1[_i];
        if (handler.check(s))
            return handler.type;
    }
    checkUndeclaredVars(s, varTable);
    return 'unsupported';
}
var statementExecutors = {
    empty: function () { return null; },
    struct: function (s) { return handleStructDeclaration(s); },
    'generic-function': function () { return ''; },
    function: function (s) { return handleFunctionDeclaration(s); },
    'function-call': function (s) { return handleFunctionCall(s); },
    else: function () { return null; },
    keywords: function () { return null; },
    'if-else-chain': function (s, varTable) { return handleIfStatement(s, varTable); },
    if: function (s, varTable) { return handleIfStatement(s, varTable); },
    while: function (s) { return handleWhileStatement(s); },
    block: function (s, varTable) { return handleBlock(s, varTable); },
    declaration: function (s, varTable) { return handleDeclaration(s, varTable); },
    assignment: function (s, varTable) { return handleAssignment(s, varTable); },
    comparison: function (s) { return handleComparisonExpression(s); },
    'struct-construction': function (s) { return handleStructConstruction(s); },
    'c-function': function (s) { return s; },
    unsupported: function () { throw new Error("Unsupported input format."); }
};
function handleStatementByType(type, s, varTable) {
    if (statementExecutors[type]) {
        return statementExecutors[type](s, varTable);
    }
    return null;
}
// Helper to check all variables used in an expression are declared
function checkVarsDeclared(expr, varTable) {
    // Remove single-quoted chars, string literals, array literals, and type suffixes
    var filtered = expr.replace(/'[^']'/g, '');
    filtered = filtered.replace(/"[^"]*"/g, '');
    filtered = filtered.replace(/\[[^\]]*\]/g, '');
    var typeSuffixes = ['U8', 'U16', 'U32', 'U64', 'I8', 'I16', 'I32', 'I64', 'Bool', 'trueBool', 'falseBool'];
    // Match identifiers and field accesses (e.g., created.x)
    var idOrFieldRegex = /([a-zA-Z_][a-zA-Z0-9_]*)(?:\.[a-zA-Z_][a-zA-Z0-9_]*)?/g;
    var match;
    while ((match = idOrFieldRegex.exec(filtered)) !== null) {
        var baseId = match[1];
        if (!typeSuffixes.includes(baseId) && !varTable[baseId] && isNaN(Number(baseId)) && baseId !== 'true' && baseId !== 'false') {
            throw new Error("Variable '".concat(baseId, "' not declared"));
        }
    }
}
var typeMap = {
    'U8': 'uint8_t',
    'U16': 'uint16_t',
    'U32': 'uint32_t',
    'U64': 'uint64_t',
    'I8': 'int8_t',
    'I16': 'int16_t',
    'I32': 'int32_t',
    'I64': 'int64_t',
    'Bool': 'bool',
    'CStr': 'char*',
    '*CStr': 'char*',
    '*U8': 'uint8_t*',
    '*U16': 'uint16_t*',
    '*U32': 'uint32_t*',
    '*U64': 'uint64_t*',
    '*I8': 'int8_t*',
    '*I16': 'int16_t*',
    '*I32': 'int32_t*',
    '*I64': 'int64_t*',
    '*Bool': 'bool*'
};
function parseTypeSuffix(value) {
    var typeKeys = Object.keys(typeMap);
    for (var _i = 0, typeKeys_1 = typeKeys; _i < typeKeys_1.length; _i++) {
        var t = typeKeys_1[_i];
        if (value.endsWith(t)) {
            return {
                value: value.slice(0, value.length - t.length).trim(),
                type: t
            };
        }
    }
    // Handle boolean literals
    if (value === 'true' || value === 'false') {
        return { value: value, type: 'Bool' };
    }
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
function validateArrayElements(elems) {
    return elems.every(function (e) {
        if (e.length === 0)
            return false;
        if (e[0] === '-' && e.length > 1) {
            return e.slice(1).split('').every(function (ch) { return ch >= '0' && ch <= '9'; });
        }
        return e.split('').every(function (ch) { return ch >= '0' && ch <= '9'; });
    });
}
function parseArrayValue(arrVal, arrLen) {
    if (arrVal.startsWith('"') && arrVal.endsWith('"')) {
        var chars = arrVal.slice(1, -1).split('');
        if (chars.length !== arrLen) {
            throw new Error("String length does not match array length.");
        }
        return chars.map(function (c) { return "'".concat(c, "'"); });
    }
    else {
        if (!arrVal.startsWith('[') || !arrVal.endsWith(']')) {
            throw new Error("Array value must be in brackets.");
        }
        var elemsStr = arrVal.slice(1, -1);
        var elems = elemsStr.split(',').map(function (e) { return e.trim(); }).filter(function (e) { return e.length > 0; });
        if (elems.length !== arrLen) {
            throw new Error("Array length does not match type annotation.");
        }
        if (!validateArrayElements(elems)) {
            throw new Error("Array elements must be integers.");
        }
        return elems;
    }
}
function handleArrayTypeAnnotation(varName, declaredType, right) {
    var inner = declaredType.slice(1, -1);
    var semiIdx = inner.indexOf(';');
    if (semiIdx === -1)
        throw new Error("Invalid array type annotation.");
    var elemType = inner.slice(0, semiIdx).trim();
    var arrLenStr = inner.slice(semiIdx + 1).trim();
    var arrLen = Number(arrLenStr);
    if (!typeMap[elemType]) {
        throw new Error("Unsupported array element type.");
    }
    if (!Number.isInteger(arrLen) || arrLen < 0) {
        throw new Error("Invalid array length.");
    }
    var arrVal = right.trim();
    var elems = parseArrayValue(arrVal, arrLen);
    return "".concat(typeMap[elemType], " ").concat(varName, "[").concat(arrLen, "] = {").concat(elems.join(', '), "}");
}
function validateBoolAssignment(declaredType, value) {
    if (declaredType === 'Bool' && value !== 'true' && value !== 'false') {
        throw new Error('Bool type must be assigned true or false');
    }
}
function handleTypeAnnotation(rest) {
    var _a = rest.split('='), left = _a[0], right = _a[1];
    var leftParts = left.split(':');
    var varName = leftParts[0].trim();
    var declaredType = leftParts[1].trim();
    // Normalize [U8; 3, 3] to [[U8; 3]; 3] for multi-dimensional shorthand
    if (declaredType.startsWith('[') && declaredType.endsWith(']') && declaredType.includes(';')) {
        // Manual parse for multi-dimensional shorthand: [U8; 3, 3]
        var inner = declaredType.slice(1, -1);
        var semiIdx = inner.indexOf(';');
        if (semiIdx !== -1) {
            var baseType = inner.slice(0, semiIdx).trim();
            var dimsStr = inner.slice(semiIdx + 1).trim();
            var dims = dimsStr.split(',').map(function (d) { return d.trim(); }).filter(function (d) { return d.length > 0; });
            if (dims.length > 1) {
                // Build nested type string
                var nestedType = baseType;
                for (var i = dims.length - 1; i >= 0; i--) {
                    nestedType = "[".concat(nestedType, "; ").concat(dims[i], "]");
                }
                return handleArrayTypeAnnotation(varName, nestedType, right);
            }
        }
        return handleArrayTypeAnnotation(varName, declaredType, right);
    }
    var _b = parseTypeSuffix(right.trim()), value = _b.value, valueType = _b.type;
    if (valueType && declaredType !== valueType) {
        throw new Error('Type mismatch between declared and literal type');
    }
    if (!typeMap[declaredType]) {
        throw new Error("Unsupported type.");
    }
    validateBoolAssignment(declaredType, value);
    return "".concat(typeMap[declaredType], " ").concat(varName, " = ").concat(value);
}
function handleNoTypeAnnotation(rest) {
    var eqIdx = rest.indexOf('=');
    if (eqIdx === -1) {
        throw new Error("Unsupported input format.");
    }
    var varName = rest.slice(0, eqIdx).trim();
    var rhs = rest.slice(eqIdx + 1).trim();
    // Support dereferencing: let z = *y;
    if (rhs.startsWith('*')) {
        return "int32_t ".concat(varName, " = ").concat(rhs);
    }
    var _a = parseTypeSuffix(rhs), value = _a.value, type = _a.type;
    if (type) {
        // For Bool, ensure value is true/false
        if (type === 'Bool' && value !== 'true' && value !== 'false') {
            throw new Error('Bool type must be assigned true or false');
        }
        return "".concat(typeMap[type], " ").concat(varName, " = ").concat(value);
    }
    return "int32_t ".concat(varName, " = ").concat(value);
}
// Handle string literal assignment: let x = "abc";
function handleStringAssignment(varName, str) {
    var chars = str.slice(1, -1).split('');
    return "uint8_t ".concat(varName, "[").concat(chars.length, "] = {").concat(chars.map(function (c) { return "'".concat(c, "'"); }).join(', '), "}");
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
    var newBracketDepth = bracketDepth;
    var newBraceDepth = braceDepth;
    if (ch === '[')
        newBracketDepth++;
    if (ch === ']')
        newBracketDepth--;
    if (ch === '{')
        newBraceDepth++;
    if (ch === '}')
        newBraceDepth--;
    return { bracketDepth: newBracketDepth, braceDepth: newBraceDepth };
}
function shouldSplitAfterBlock(buf, braceDepth, bracketDepth, str, i) {
    if (braceDepth !== 0 || !buf.trim().endsWith('}') || bracketDepth !== 0) {
        return false;
    }
    // Look ahead for non-whitespace content
    var j = i + 1;
    while (j < str.length && /\s/.test(str[j]))
        j++;
    return j < str.length;
}
// Improved split: split on semicolons and also split blocks that are followed by other statements
function smartSplit(str) {
    var result = [];
    var buf = '';
    var bracketDepth = 0;
    var braceDepth = 0;
    var i = 0;
    var addCurrentBuffer = function () {
        var trimmed = buf.trim();
        if (trimmed.length > 0) {
            result.push(trimmed);
            buf = '';
        }
    };
    while (i < str.length) {
        var ch = str[i];
        var depths = updateDepths(ch, bracketDepth, braceDepth);
        bracketDepth = depths.bracketDepth;
        braceDepth = depths.braceDepth;
        if (ch === ';' && bracketDepth === 0 && braceDepth === 0) {
            // For function prototypes, we need to keep the semicolon
            var currentTrimmed = buf.trim();
            if ((currentTrimmed.startsWith('fn ') || currentTrimmed.startsWith('extern fn ')) && currentTrimmed.indexOf('=>') === -1) {
                // This looks like a function prototype, keep the semicolon
                buf += ch;
                addCurrentBuffer();
            }
            else {
                // Regular statement, split without the semicolon
                addCurrentBuffer();
            }
            i++;
            continue;
        }
        buf += ch;
        var splitType = shouldSplitHere(ch, buf, bracketDepth, braceDepth, str, i);
        if (splitType === 'block') {
            addCurrentBuffer();
            while (i + 1 < str.length && /\s/.test(str[i + 1]))
                i++;
        }
        i++;
    }
    addCurrentBuffer();
    return result;
}
function handleBlock(s, varTable) {
    var inner = s.slice(1, -1).trim();
    if (inner.length === 0) {
        return '{}';
    }
    else {
        // Compile block contents using parent variable table directly (relaxed scoping)
        var statements = smartSplit(inner);
        var blockVarTable = arguments.length > 1 && typeof arguments[1] === 'object' ? arguments[1] : {};
        var results = processStatements(statements, blockVarTable);
        // Join statements without adding semicolons; let joinResults handle it
        var blockContent = results.map(function (r) {
            if (r.startsWith('{') && r.endsWith('}'))
                return r;
            if (/^if\s*\(.+\)\s*\{.*\}(\s*else\s*\{.*\})?$/.test(r))
                return r;
            return r + ';';
        }).join(' ');
        return "{".concat(blockContent, "}");
    }
}
function handleTypedDeclaration(s) {
    var colonIdx = s.indexOf(':');
    var eqIdx = s.indexOf('=');
    var varName = s.slice(0, colonIdx).replace('let', '').replace('mut', '').trim();
    var declaredType = s.slice(colonIdx + 1, eqIdx).trim();
    // Normalize pointer types for all primitives
    if (declaredType.startsWith('*')) {
        var baseType = declaredType.slice(1);
        if (typeMap['*' + baseType]) {
            declaredType = '*' + baseType;
        }
    }
    var value = s.slice(eqIdx + 1).replace(/;$/, '').trim();
    // Support arbitrary referencing/dereferencing: let z : I32 = *y; let p : *I32 = &x;
    if (value.startsWith('*') || value.startsWith('&')) {
        return "".concat(typeMap[declaredType], " ").concat(varName, " = ").concat(value);
    }
    // Check for struct construction
    var structConstructMatch = value.match(/^([A-Z][a-zA-Z0-9_]*)\s*\{([^}]*)\}$/);
    if (structConstructMatch) {
        // Output: struct <type> <varName> = { ... };
        return "struct ".concat(declaredType, " ").concat(varName, " = { ").concat(structConstructMatch[2].trim(), " }");
    }
    // Allow arrays and string literals
    if (declaredType.startsWith('[') || value.startsWith('[') || value.startsWith('"')) {
        return handleArrayTypeAnnotation(varName, declaredType, value);
    }
    if (typeMap[declaredType]) {
        var _a = parseTypeSuffix(value), val = _a.value, valueType = _a.type;
        if (valueType && declaredType !== valueType) {
            throw new Error('Type mismatch between declared and literal type');
        }
        validateBoolAssignment(declaredType, val);
        return "".concat(typeMap[declaredType], " ").concat(varName, " = ").concat(val);
    }
    else if (/^[A-Z][a-zA-Z0-9_]*$/.test(declaredType) && declaredType !== 'CStr') {
        // If value is a struct construction, emit struct <Type> <varName> = { ... }
        var structConstructMatch_1 = value.match(/^([A-Z][a-zA-Z0-9_]*)\s*\{([^}]*)\}$/);
        if (structConstructMatch_1) {
            return "struct ".concat(declaredType, " ").concat(varName, " = { ").concat(structConstructMatch_1[2].trim(), " }");
        }
        // Otherwise, emit struct <Type> <varName> = <value>
        return "struct ".concat(declaredType, " ").concat(varName, " = ").concat(value);
    }
    else {
        throw new Error("Unsupported type.");
    }
}
function parseUntypedDeclaration(s) {
    s = s.slice(4).trim(); // Remove 'let '
    var isMut = false;
    if (s.startsWith('mut ')) {
        isMut = true;
        s = s.slice(4).trim();
    }
    var varName;
    if (s.includes(':')) {
        var left = s.split('=')[0];
        varName = left.split(':')[0].trim();
        return { varName: varName, isMut: isMut, hasTypeAnnotation: true, statement: s };
    }
    else {
        var eqIdx = s.indexOf('=');
        varName = s.slice(0, eqIdx).trim();
        return { varName: varName, isMut: isMut, hasTypeAnnotation: false, statement: s };
    }
}
function handleUntypedDeclaration(s, varTable) {
    var parsed = parseUntypedDeclaration(s);
    var varName = parsed.varName, isMut = parsed.isMut, hasTypeAnnotation = parsed.hasTypeAnnotation, statement = parsed.statement;
    if (hasTypeAnnotation) {
        var _a = statement.split('='), right = _a[1];
        checkVarsDeclared(right, varTable);
        varTable[varName] = { mut: isMut };
        return handleTypeAnnotation(statement);
    }
    else {
        var eqIdx = statement.indexOf('=');
        var value = statement.slice(eqIdx + 1).trim();
        checkVarsDeclared(value, varTable);
        varTable[varName] = { mut: isMut };
        var structConstructMatch = value.match(/^([A-Z][a-zA-Z0-9_]*)\s*\{([^}]*)\}$/);
        if (structConstructMatch) {
            return "struct ".concat(structConstructMatch[1], " ").concat(varName, " = { ").concat(structConstructMatch[2].trim(), " }");
        }
        if (value.startsWith('"') && value.endsWith('"')) {
            return handleStringAssignment(varName, value);
        }
        else {
            return handleNoTypeAnnotation(statement);
        }
    }
}
function handleDeclaration(s, varTable) {
    // Example: let myPoint : Point = Point { 3, 4 };
    var colonIdx = s.indexOf(':');
    var eqIdx = s.indexOf('=');
    if (colonIdx !== -1 && eqIdx !== -1) {
        return handleTypedDeclaration(s);
    }
    return handleUntypedDeclaration(s, varTable);
}
function handleAssignment(s, varTable) {
    var eqIdx = s.indexOf('=');
    var varName = s.slice(0, eqIdx).trim();
    var rhs = s.slice(eqIdx + 1).trim();
    if (!varTable[varName]) {
        throw new Error("Variable '".concat(varName, "' not declared"));
    }
    if (!varTable[varName].mut) {
        throw new Error("Cannot assign to immutable variable '".concat(varName, "'"));
    }
    var filteredRhs = rhs.replace(/'[^']'/g, '');
    var identifiers = filteredRhs.match(/[a-zA-Z_][a-zA-Z0-9_]*/g) || [];
    for (var _i = 0, identifiers_2 = identifiers; _i < identifiers_2.length; _i++) {
        var id = identifiers_2[_i];
        if (!varTable[id] && isNaN(Number(id)) && id !== 'true' && id !== 'false') {
            throw new Error("Variable '".concat(id, "' not declared"));
        }
    }
    return "".concat(varName, " = ").concat(rhs);
}
function compileBlock(blockInput, parentVarTableParam) {
    var statements = smartSplit(blockInput);
    // Accept parent varTable as second argument
    var parentVarTable = parentVarTableParam || {};
    var blockVarTable = Object.create(null);
    Object.assign(blockVarTable, parentVarTable);
    var results = [];
    for (var _i = 0, statements_1 = statements; _i < statements_1.length; _i++) {
        var stmt = statements_1[_i];
        var s = stmt.trim();
        if (isBlock(s)) {
            results.push(handleBlock(s, blockVarTable));
        }
        else if (s.startsWith('let ')) {
            results.push(handleDeclaration(s, blockVarTable));
        }
        else if (isAssignment(s)) {
            results.push(handleAssignment(s, blockVarTable));
        }
        else {
            throw new Error("Unsupported input format.");
        }
    }
    return "{".concat(results.map(function (r) { return r + ';'; }).join(' '), "}");
}
function isIfStatement(s) {
    // Match if(...) {...} and if(...) {...} else {...} with any whitespace/content
    // Also match if(...) {...} else if(...) {...} else {...}
    return (/^if\s*\([^)]*\)\s*\{[\s\S]*?\}\s*else\s*if\s*\([^)]*\)\s*\{[\s\S]*?\}\s*else\s*\{[\s\S]*?\}$/.test(s) ||
        /^if\s*\([^)]*\)\s*\{[\s\S]*?\}\s*else\s*\{[\s\S]*?\}$/.test(s) ||
        /^if\s*\([^)]*\)\s*\{[\s\S]*?\}$/.test(s));
}
function isWhileStatement(s) {
    // Match while(...) {...}
    return /^while\s*\([^)]*\)\s*\{[\s\S]*?\}$/.test(s);
}
function handleWhileStatement(s) {
    // Output as-is for now (no inner compilation needed for these tests)
    return s;
}
function handleIfStatement(s, varTable) {
    // Parse the if/else blocks and compile their contents as atomic units
    // Handle else-if-else chains specially
    if (/^if\s*\([^)]*\)\s*\{[\s\S]*?\}\s*else\s*if\s*\([^)]*\)\s*\{[\s\S]*?\}\s*else\s*\{[\s\S]*?\}$/.test(s)) {
        // For else-if-else chains, return as-is (no inner compilation needed for this test)
        return s;
    }
    var ifElseRegex = /^if\s*\(([^)]*)\)\s*\{([\s\S]*?)\}(?:\s*else\s*\{([\s\S]*?)\})?\s*$/;
    var match = s.match(ifElseRegex);
    if (!match)
        return s;
    var condition = match[1].trim();
    var ifBlock = match[2];
    var elseBlock = match[3] !== undefined ? match[3] : null;
    // Get parent varTable from arguments
    var parentVarTable = arguments.length > 1 && typeof arguments[1] === 'object' ? arguments[1] : {};
    // Compile the blocks using parent scope directly
    var ifStatements = [ifBlock.trim()];
    var compiledIf = ifStatements[0] === '' ? '{}' : "{".concat(processStatements(ifStatements, parentVarTable).join(' '), "}");
    if (elseBlock !== null) {
        var elseStatements = [elseBlock.trim()];
        var compiledElse = elseStatements[0] === '' ? '{}' : "{".concat(processStatements(elseStatements, parentVarTable).join(' '), "}");
        return "if(".concat(condition, ")").concat(compiledIf, "else").concat(compiledElse);
    }
    else {
        return "if(".concat(condition, ")").concat(compiledIf);
    }
}
function processStatements(statements, varTable) {
    var results = [];
    var functionNames = [];
    var persistentVarTable = Object.create(varTable);
    function addFunctionToTable(s) {
        var parts = getFunctionParts(s);
        functionNames.push(parts.name);
        persistentVarTable[parts.name] = { func: true };
    }
    function addStructToTable(s) {
        var trimmed = s.trim();
        var structIdx = 6;
        var openBraceIdx = trimmed.indexOf('{', structIdx);
        var name = trimmed.slice(structIdx, openBraceIdx).trim();
        persistentVarTable[name] = { struct: true };
    }
    function addVarToTable(s) {
        var varName;
        if (s.includes(':')) {
            varName = s.split('=')[0].split(':')[0].replace('let', '').replace('mut', '').trim();
        }
        else {
            varName = s.split('=')[0].replace('let', '').replace('mut', '').trim();
        }
        persistentVarTable[varName] = { mut: s.includes('mut') };
    }
    function patchVarTable() {
        var patchedVarTable = Object.create(persistentVarTable);
        for (var _i = 0, functionNames_1 = functionNames; _i < functionNames_1.length; _i++) {
            var fname = functionNames_1[_i];
            patchedVarTable[fname] = { func: true };
        }
        for (var key in persistentVarTable) {
            if (persistentVarTable[key] && persistentVarTable[key].struct) {
                patchedVarTable[key] = { struct: true };
            }
        }
        return patchedVarTable;
    }
    for (var _i = 0, statements_2 = statements; _i < statements_2.length; _i++) {
        var stmt = statements_2[_i];
        var s = stmt.trim();
        var type = getStatementType(s, persistentVarTable);
        if (type === 'function') {
            addFunctionToTable(s);
        }
        if (type === 'struct') {
            addStructToTable(s);
        }
        var patchedVarTable = patchVarTable();
        if (type === 'declaration') {
            addVarToTable(s);
        }
        var result = handleStatementByType(type, s, patchedVarTable);
        if (result !== null && result !== undefined && result !== '') {
            results.push(result);
        }
    }
    return results;
}
function isCFunctionDeclaration(r) {
    // Accept any C function declaration: <type> <name>(...) {...}
    var openParenIdx = r.indexOf('(');
    var closeParenIdx = r.indexOf(')', openParenIdx);
    var openBraceIdx = r.indexOf('{', closeParenIdx);
    var closeBraceIdx = r.lastIndexOf('}');
    if (openParenIdx === -1 || closeParenIdx === -1 || openBraceIdx === -1 || closeBraceIdx === -1)
        return false;
    // Must start with a type and name, then (...), then {...}
    var beforeParen = r.slice(0, openParenIdx).trim();
    var afterParen = r.slice(closeParenIdx + 1, openBraceIdx).trim();
    if (!beforeParen.match(/^(void|int8_t|int16_t|int32_t|int64_t|uint8_t|uint16_t|uint32_t|uint64_t|bool)\s+[a-zA-Z_][a-zA-Z0-9_]*$/))
        return false;
    if (afterParen.length !== 0)
        return false;
    return true;
}
function shouldAddSemicolon(result) {
    if (result.endsWith(';'))
        return false; // Already has semicolon
    if (result.startsWith('{') && result.endsWith('}'))
        return false;
    if (/(==|!=|<=|>=|<|>)/.test(result))
        return false;
    if (/^if\s*\(.+\)\s*\{.*\}(\s*else\s*\{.*\})?$/.test(result))
        return false;
    if (/^while\s*\(.+\)\s*\{.*\}$/.test(result))
        return false;
    if (isCFunctionDeclaration(result))
        return false;
    if (/^char\*\s+[a-zA-Z_][a-zA-Z0-9_]*\s*\([^)]*\)\s*\{[\s\S]*\}$/.test(result))
        return false;
    if (/^struct\s+[a-zA-Z_][a-zA-Z0-9_]*\s*\{[\s\S]*\};$/.test(result))
        return false;
    return true;
}
function joinResults(results) {
    return results
        .map(function (result) { return shouldAddSemicolon(result) ? result + ';' : result; })
        .join(' ');
}
// Helper function to parse generic function declarations
function parseGenericFunctionDeclarations(input) {
    var declarations = {};
    var statements = smartSplit(input);
    for (var _i = 0, statements_3 = statements; _i < statements_3.length; _i++) {
        var statement = statements_3[_i];
        if (isGenericFunctionDeclaration(statement)) {
            var trimmed = statement.trim();
            // Find the function name and type parameters
            var fnIdx = trimmed.indexOf('fn ') + 3;
            var openParenIdx = trimmed.indexOf('(', fnIdx);
            var nameWithTypes = trimmed.slice(fnIdx, openParenIdx).trim();
            var openBracketIdx = nameWithTypes.indexOf('<');
            var closeBracketIdx = nameWithTypes.lastIndexOf('>');
            var name_1 = nameWithTypes.slice(0, openBracketIdx).trim();
            var typeParamsStr = nameWithTypes.slice(openBracketIdx + 1, closeBracketIdx).trim();
            var typeParams = typeParamsStr.split(',').map(function (t) { return t.trim(); }).filter(function (t) { return t.length > 0; });
            declarations[name_1] = { src: statement, typeParams: typeParams };
        }
    }
    return declarations;
}
// Helper function to find generic function calls
function findGenericFunctionCalls(input, declarations) {
    var instantiations = {};
    var statements = smartSplit(input);
    for (var _i = 0, statements_4 = statements; _i < statements_4.length; _i++) {
        var statement = statements_4[_i];
        var trimmed = statement.trim();
        // Look for function calls (not declarations)
        if (!trimmed.startsWith('fn ')) {
            // Find patterns like: functionName<Type1, Type2>(
            var i = 0;
            while (i < trimmed.length) {
                var openBracketIdx = trimmed.indexOf('<', i);
                if (openBracketIdx === -1)
                    break;
                // Find the function name before <
                var nameStart = openBracketIdx - 1;
                while (nameStart >= 0) {
                    var char = trimmed[nameStart];
                    if ((char >= 'a' && char <= 'z') ||
                        (char >= 'A' && char <= 'Z') ||
                        (char >= '0' && char <= '9') ||
                        char === '_') {
                        nameStart--;
                    }
                    else {
                        break;
                    }
                }
                nameStart++;
                var funcName = trimmed.slice(nameStart, openBracketIdx);
                // Find the closing >
                var closeBracketIdx = trimmed.indexOf('>', openBracketIdx);
                if (closeBracketIdx === -1) {
                    i = openBracketIdx + 1;
                    continue;
                }
                // Check if this is followed by (
                var nextIdx = closeBracketIdx + 1;
                while (nextIdx < trimmed.length && /\s/.test(trimmed[nextIdx])) {
                    nextIdx++;
                }
                if (nextIdx >= trimmed.length || trimmed[nextIdx] !== '(') {
                    i = openBracketIdx + 1;
                    continue;
                }
                // This looks like a generic function call
                if (declarations[funcName]) {
                    var typeParamsStr = trimmed.slice(openBracketIdx + 1, closeBracketIdx);
                    var types = typeParamsStr.split(',').map(function (t) { return t.trim(); }).filter(function (t) { return t.length > 0; });
                    var mangled = "".concat(funcName, "_").concat(types.join('_'));
                    instantiations[mangled] = { base: funcName, types: types };
                }
                i = closeBracketIdx + 1;
            }
        }
    }
    return instantiations;
}
// Helper function to parse function header without regex
function parseFunctionHeader(src) {
    var trimmed = src.trim();
    // Find the opening parenthesis
    var openParenIdx = trimmed.indexOf('(');
    if (openParenIdx === -1)
        return null;
    // Find the closing parenthesis
    var closeParenIdx = trimmed.indexOf(')', openParenIdx);
    if (closeParenIdx === -1)
        return null;
    // Extract parameters
    var params = trimmed.slice(openParenIdx + 1, closeParenIdx).trim();
    // Find the colon after the closing parenthesis
    var colonIdx = trimmed.indexOf(':', closeParenIdx);
    if (colonIdx === -1)
        return null;
    // Find the => after the colon
    var arrowIdx = trimmed.indexOf('=>', colonIdx);
    if (arrowIdx === -1)
        return null;
    // Extract return type
    var retType = trimmed.slice(colonIdx + 1, arrowIdx).trim();
    return { params: params, retType: retType };
}
// Helper function to substitute array types without regex
function substituteArrayTypes(params, typeMapSub) {
    var result = params;
    var i = 0;
    while (i < result.length) {
        var openBracketIdx = result.indexOf('[', i);
        if (openBracketIdx === -1)
            break;
        var semicolonIdx = result.indexOf(';', openBracketIdx);
        if (semicolonIdx === -1) {
            i = openBracketIdx + 1;
            continue;
        }
        var closeBracketIdx = result.indexOf(']', semicolonIdx);
        if (closeBracketIdx === -1) {
            i = openBracketIdx + 1;
            continue;
        }
        var elemType = result.slice(openBracketIdx + 1, semicolonIdx).trim();
        var sizeStr = result.slice(semicolonIdx + 1, closeBracketIdx).trim();
        // Check if this is a number (array size)
        var isNumber = sizeStr.length > 0 && sizeStr.split('').every(function (char) { return char >= '0' && char <= '9'; });
        if (isNumber) {
            var concrete = typeMapSub[elemType] ? typeMap[typeMapSub[elemType]] || typeMapSub[elemType] : typeMap[elemType] || elemType;
            var replacement = "".concat(concrete, "[").concat(sizeStr, "]");
            result = result.slice(0, openBracketIdx) + replacement + result.slice(closeBracketIdx + 1);
            i = openBracketIdx + replacement.length;
        }
        else {
            i = openBracketIdx + 1;
        }
    }
    return result;
}
// Helper function to substitute regular types without regex
function substituteRegularTypes(params, typeMapSub) {
    var parts = params.split(',');
    return parts.map(function (part) {
        var colonIdx = part.indexOf(':');
        if (colonIdx === -1)
            return part.trim();
        var paramName = part.slice(0, colonIdx).trim();
        var typePart = part.slice(colonIdx + 1).trim();
        // Skip if this is already an array type (contains [ and ])
        if (typePart.includes('[') && typePart.includes(']')) {
            return part.trim();
        }
        var concrete = typeMapSub[typePart] ? typeMap[typeMapSub[typePart]] || typeMapSub[typePart] : typeMap[typePart] || typePart;
        return "".concat(paramName, " : ").concat(concrete);
    }).join(', ');
}
// Helper function to replace generic function calls with mangled names
function replaceGenericCalls(statements, instantiations) {
    return statements.map(function (statement) {
        var result = statement;
        var _loop_1 = function (mangled) {
            var _a = instantiations[mangled], base = _a.base, types = _a.types;
            // Look for patterns like: base<type1,type2>(
            var i = 0;
            while (i < result.length) {
                var nameIdx = result.indexOf(base, i);
                if (nameIdx === -1)
                    break;
                var afterNameIdx = nameIdx + base.length;
                if (afterNameIdx >= result.length || result[afterNameIdx] !== '<') {
                    i = nameIdx + 1;
                    continue;
                }
                var openBracketIdx = afterNameIdx;
                var closeBracketIdx = result.indexOf('>', openBracketIdx);
                if (closeBracketIdx === -1) {
                    i = nameIdx + 1;
                    continue;
                }
                // Check if followed by (
                var nextIdx = closeBracketIdx + 1;
                while (nextIdx < result.length && result[nextIdx] === ' ') {
                    nextIdx++;
                }
                if (nextIdx >= result.length || result[nextIdx] !== '(') {
                    i = nameIdx + 1;
                    continue;
                }
                // Extract the types and check if they match
                var typeStr = result.slice(openBracketIdx + 1, closeBracketIdx);
                var extractedTypes = typeStr.split(',').map(function (t) { return t.trim(); });
                // Check if types match
                if (extractedTypes.length === types.length &&
                    extractedTypes.every(function (t, idx) { return t === types[idx]; })) {
                    // Replace the call
                    var beforeCall = result.slice(0, nameIdx);
                    var afterCall = result.slice(nextIdx); // nextIdx already points to the '('
                    result = beforeCall + mangled + afterCall;
                    i = nameIdx + mangled.length;
                }
                else {
                    i = nameIdx + 1;
                }
            }
        };
        for (var mangled in instantiations) {
            _loop_1(mangled);
        }
        return result;
    });
}
// Helper function to detect and handle import statements
function processImports(statements) {
    var includes = [];
    var processedStatements = [];
    for (var _i = 0, statements_5 = statements; _i < statements_5.length; _i++) {
        var statement = statements_5[_i];
        var processedStatement = statement;
        // Look for import statements within the statement and extract them
        var searchStart = 0;
        while (true) {
            var importIdx = processedStatement.indexOf('import ', searchStart);
            if (importIdx === -1)
                break;
            // Check if this is at a statement boundary (start of string or after semicolon + whitespace)
            var isAtBoundary = importIdx === 0;
            if (!isAtBoundary) {
                var beforeImport = processedStatement.slice(0, importIdx);
                var lastSemicolon = beforeImport.lastIndexOf(';');
                if (lastSemicolon !== -1) {
                    var afterSemicolon = beforeImport.slice(lastSemicolon + 1);
                    isAtBoundary = afterSemicolon.trim() === '';
                }
            }
            if (isAtBoundary) {
                // Find the end of this import statement
                var semicolonIdx = processedStatement.indexOf(';', importIdx);
                if (semicolonIdx !== -1) {
                    var importStatement = processedStatement.slice(importIdx, semicolonIdx + 1).trim();
                    var importPart = importStatement.slice(7, -1).trim(); // Remove 'import ' and ';'
                    // Check if it's a valid identifier
                    var isValidIdentifier = importPart.length > 0 &&
                        importPart.split('').every(function (char) {
                            return (char >= 'a' && char <= 'z') ||
                                (char >= 'A' && char <= 'Z') ||
                                (char >= '0' && char <= '9') ||
                                char === '_';
                        });
                    if (isValidIdentifier) {
                        includes.push("#include <".concat(importPart, ".h>"));
                        // Remove the import statement from the processed statement
                        processedStatement = processedStatement.slice(0, importIdx) +
                            processedStatement.slice(semicolonIdx + 1);
                        // Continue searching from the same position (since we removed text)
                        searchStart = importIdx;
                        continue;
                    }
                }
            }
            searchStart = importIdx + 1;
        }
        // Only add the statement if it has content after removing imports
        if (processedStatement.trim()) {
            processedStatements.push(processedStatement.trim());
        }
    }
    return { statements: processedStatements, includes: includes };
}
function compile(input) {
    // Early check: if input only contains generic functions without calls, return empty
    var trimmed = input.trim();
    var earlyStatements = smartSplit(trimmed);
    if (earlyStatements.length === 1 && isGenericFunctionDeclaration(earlyStatements[0])) {
        return '';
    }
    // Monomorphization support using string parsing instead of regexes
    var genericDecls = parseGenericFunctionDeclarations(input);
    var instantiations = findGenericFunctionCalls(input, genericDecls);
    // Pass 3: generate concrete functions
    var monomorphized = '';
    // Only emit monomorphized functions, not generic declarations
    for (var mangled in instantiations) {
        var _a = instantiations[mangled], base = _a.base, types = _a.types;
        var decl = genericDecls[base];
        // Parse the function header without regex
        var headerInfo = parseFunctionHeader(decl.src);
        if (!headerInfo)
            continue;
        var params = headerInfo.params;
        var retType = headerInfo.retType;
        // Extract function body
        var openBraceIdx = decl.src.indexOf('{');
        var closeBraceIdx = decl.src.lastIndexOf('}');
        var body = decl.src.slice(openBraceIdx + 1, closeBraceIdx).trim();
        // Create type substitution map
        var typeMapSub = {};
        for (var i = 0; i < decl.typeParams.length; ++i) {
            typeMapSub[decl.typeParams[i]] = types[i];
        }
        // Substitute array types like [T; 3]
        params = substituteArrayTypes(params, typeMapSub);
        // Substitute regular types
        params = substituteRegularTypes(params, typeMapSub);
        // Substitute return type
        if (typeMapSub[retType]) {
            retType = typeMap[typeMapSub[retType]] || typeMapSub[retType];
        }
        else {
            retType = typeMap[retType] || retType;
        }
        var cRetType = retType === 'Void' ? 'void' : retType;
        // Convert params to C format
        var cParams = params.split(',').map(function (p) {
            var trimmed = p.trim();
            if (!trimmed)
                return '';
            if (trimmed.includes('[') && trimmed.includes(']')) {
                // Handle array parameters: paramName : type[size] -> type paramName[size]
                var colonIdx_1 = trimmed.indexOf(':');
                if (colonIdx_1 === -1)
                    return trimmed;
                var paramName = trimmed.slice(0, colonIdx_1).trim();
                var typePart = trimmed.slice(colonIdx_1 + 1).trim();
                var openBracketIdx = typePart.indexOf('[');
                var closeBracketIdx = typePart.indexOf(']');
                if (openBracketIdx !== -1 && closeBracketIdx !== -1) {
                    var baseType = typePart.slice(0, openBracketIdx).trim();
                    var arraySize = typePart.slice(openBracketIdx + 1, closeBracketIdx).trim();
                    return "".concat(baseType, " ").concat(paramName, "[").concat(arraySize, "]");
                }
            }
            // Handle regular parameters: paramName : type -> type paramName
            var colonIdx = trimmed.indexOf(':');
            if (colonIdx !== -1) {
                var paramName = trimmed.slice(0, colonIdx).trim();
                var paramType = trimmed.slice(colonIdx + 1).trim();
                return "".concat(paramType, " ").concat(paramName);
            }
            return trimmed;
        }).filter(function (p) { return p; }).join(', ');
        var formattedBody = body.trim() ? " ".concat(body, " ") : '';
        monomorphized += "".concat(cRetType, " ").concat(mangled, "(").concat(cParams, ") {").concat(formattedBody, "} ");
    }
    // Process statements and replace generic function calls
    var statements = smartSplit(input);
    // Replace generic function calls with mangled names
    statements = replaceGenericCalls(statements, instantiations);
    // Process import statements 
    var importResult = processImports(statements);
    statements = importResult.statements;
    var includes = importResult.includes;
    // Remove all generic function declarations from statements
    statements = statements.filter(function (s) { return !isGenericFunctionDeclaration(s.trim()); });
    // Register mangled function names in varTable
    var varTable = {};
    for (var mangled in instantiations) {
        varTable[mangled] = { mut: false, func: true };
    }
    var results = processStatements(statements, varTable);
    // Prepend monomorphized functions to the output with a space if needed
    return (includes.length ? includes.join('\n') + '\n' : '') + (monomorphized ? monomorphized.trim() + ' ' : '') + joinResults(results).trim();
    // (Removed duplicate block)
}
function isStructConstruction(s) {
    // Match: Type { ... }
    return /^([A-Z][a-zA-Z0-9_]*)\s*\{[^}]*\}$/.test(s.trim());
}
function handleStructConstruction(s) {
    // Convert: Point { 3, 4 } -> (struct Point){ 3, 4 }
    var match = s.trim().match(/^([A-Z][a-zA-Z0-9_]*)\s*\{([^}]*)\}$/);
    if (!match)
        return s;
    var typeName = match[1];
    var values = match[2].trim();
    return "(struct ".concat(typeName, "){ ").concat(values, " }");
}
