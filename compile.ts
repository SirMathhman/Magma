function isLetter(ch: string) {
  const code = ch.charCodeAt(0);
  return (code >= 65 && code <= 90) || (code >= 97 && code <= 122);
}

function isDigit(ch: string) {
  const code = ch.charCodeAt(0);
  return code >= 48 && code <= 57;
}

function isValidIdentifier(name: string) {
  if (!name || name.length === 0) return false;
  const first = name[0];
  if (!(isLetter(first) || first === '_')) return false;
  for (let i = 1; i < name.length; i++) {
    const ch = name[i];
    if (!(isLetter(ch) || isDigit(ch) || ch === '_')) return false;
  }
  return true;
}

function parseLetBody(body: string): { name: string; typeName: string | null; value: string } {
  const eqIndex = body.indexOf("=");
  if (eqIndex === -1) throw new Error("Invalid let declaration");

  const colonIndex = body.indexOf(":");
  let name: string;
  let typeName: string | null = null;
  if (colonIndex !== -1 && colonIndex < eqIndex) {
    name = body.substring(0, colonIndex).trim();
    typeName = body.substring(colonIndex + 1, eqIndex).trim();
  } else {
    name = body.substring(0, eqIndex).trim();
  }

  const value = body.substring(eqIndex + 1).trim();
  return { name, typeName, value };
}

// formatTypedValue removed — integer suffix handling is done inline where needed.

const supportedTypes = [
  "U8",
  "U16",
  "U32",
  "U64",
  "USize",
  "I8",
  "I16",
  "I32",
  "I64",
  "Bool",
  "Void",
  "F32",
  "F64",
];

const typeMap: { [k: string]: string } = {
  I8: "int8_t",
  I16: "int16_t",
  I32: "int32_t",
  I64: "int64_t",
  U8: "uint8_t",
  U16: "uint16_t",
  U32: "uint32_t",
  U64: "uint64_t",
  Bool: "bool",
  Void: "void",
  "*CStr": "char*",
  F32: "float",
  F64: "double",
  USize: "size_t",
};

function determineKind(typeName: string | null | undefined): 'int' | 'uint' | 'float' | 'bool' | 'void' | 'ptr' | 'other' {
  if (!typeName) return 'other';
  if (typeName === 'Bool') return 'bool';
  if (typeName === 'Void') return 'void';
  if (typeName === '*CStr') return 'ptr';
  if (typeName[0] === 'I') return 'int';
  if (typeName[0] === 'U') return 'uint';
  if (typeName === 'F32' || typeName === 'F64') return 'float';
  return 'other';
}

function getTypeInfo(typeName: string): { cType: string; usesStdint: boolean; usesStdbool: boolean; kind: 'int' | 'uint' | 'float' | 'bool' | 'void' | 'ptr' | 'other' } {
  if (!typeName) return { cType: '', usesStdint: false, usesStdbool: false, kind: 'other' };
  // USize maps to C's size_t and should not trigger stdint include
  if (typeName === 'USize') return { cType: 'size_t', usesStdint: false, usesStdbool: false, kind: 'uint' };
  const cType = typeMap[typeName] || typeName;
  const kind = determineKind(typeName);
  const usesStdint = (kind === 'int' || kind === 'uint');
  const usesStdbool = (kind === 'bool');
  return { cType, usesStdint, usesStdbool, kind };
}

// (removed) looksLikeFloatLiteral was replaced by numericKind-based detection

function numericKind(value: string): { kind: "int" | "float" | "unknown"; suffix: string } {
  const scan = scanNumericPrefix(value);
  if (!scan.hasDigitsBefore && !scan.hasDigitsAfter) return { kind: "unknown", suffix: value.substring(scan.index) };
  const suffix = value.substring(scan.index);
  return scan.hasDot ? { kind: "float", suffix } : { kind: "int", suffix };
}

function scanNumericPrefix(value: string): { index: number; hasDigitsBefore: boolean; hasDot: boolean; hasDigitsAfter: boolean } {
  if (value.length === 0) return { index: 0, hasDigitsBefore: false, hasDot: false, hasDigitsAfter: false };
  let i = 0;
  if ((value[0] === '+' || value[0] === '-') && value.length > 1) i = 1;

  let hasDigitsBefore = false;
  while (i < value.length && isDigit(value[i])) {
    i++;
    hasDigitsBefore = true;
  }

  const frac = scanFractionPart(value, i);
  return { index: frac.index, hasDigitsBefore, hasDot: frac.hasDot, hasDigitsAfter: frac.hasDigitsAfter };
}

function scanFractionPart(value: string, startIndex: number): { index: number; hasDot: boolean; hasDigitsAfter: boolean } {
  let i = startIndex;
  if (i >= value.length || value[i] !== '.') return { index: i, hasDot: false, hasDigitsAfter: false };
  // consume '.'
  i++;
  let hasDigitsAfter = false;
  while (i < value.length && isDigit(value[i])) {
    i++;
    hasDigitsAfter = true;
  }
  return { index: i, hasDot: true, hasDigitsAfter };
}
type DeclResult = { text: string; usesStdint: boolean; usesStdbool: boolean; declaredType?: string };

export function compile(input: string) {
  const src = input.trim();
  if (src === "") return "";

  const importHandled = tryHandleImport(src);
  if (importHandled !== null) return importHandled;

  const fnPrefix = "fn ";
  const externHandled = tryHandleExtern(src);
  if (externHandled !== null) return externHandled;
  if (src.startsWith(fnPrefix)) {
    const res = compileFunction(src);
    return emitWithIncludes(res);
  }

  // support top-level if statements like: if(true){}
  // If the input is a single top-level if (no semicolons), handle it directly.
  if (isIfStatement(src) && src.indexOf(';') === -1) {
    const res = compileIfStatement(src);
    return emitWithIncludes(res);
  }

  const letPrefix = "let ";
  // support multi-statement inputs (e.g. 'let x = 0; if(x){}') by routing to compileStatements
  if (src.indexOf(';') === -1 && !src.startsWith(letPrefix)) {
    throw new Error("compile only supports empty input or simple let declarations");
  }
  const results = compileStatements(src);
  // If any declaration needs stdint, emit include once at top.
  const needStdint = results.some(r => r.usesStdint);
  const needStdbool = results.some(r => r.usesStdbool);
  const decls = results.map(r => r.text).join(" ");
  return emitWithIncludes({ text: decls, usesStdint: needStdint, usesStdbool: needStdbool });
}

function emitWithIncludes(res: { text: string; usesStdint: boolean; usesStdbool: boolean }) {
  const includes: string[] = [];
  if (res.usesStdbool) includes.push('#include <stdbool.h>');
  if (res.usesStdint) includes.push('#include <stdint.h>');
  if (includes.length === 0) return res.text;
  return includes.join('\n') + '\n' + res.text;
}

function tryHandleExtern(src: string): string | null {
  const externPrefix = 'extern fn ';
  if (!src.startsWith(externPrefix)) return null;
  // extern functions have no body and must declare a return type: `extern fn name(params) : Type;`
  const afterExtern = src.substring('extern '.length).trim();
  const { /* name, params, */ afterParams } = parseFunctionHeader(afterExtern);
  const rest = afterParams.trim();
  if (!rest.startsWith(':')) throw new Error('Missing return type for extern function');
  // must end with semicolon and have no body
  if (!rest.endsWith(';')) throw new Error('Invalid extern function declaration');
  // accept but emit nothing (external linkage)
  return '';
}

function tryHandleImport(src: string): string | null {
  // simple import handling: `import string;` -> include string.h with CRLF
  if (!src.startsWith('import ')) return null;
  const rest = src.substring('import '.length).trim();
  if (rest === 'string;' || rest === 'string ;') return '#include <string.h>\r\n';
  throw new Error('Unsupported import');
}

function compileStatements(src: string): DeclResult[] {
  const letPrefix = "let ";
  const parts = src.split(";");
  const results: DeclResult[] = [];
  // symbol table tracks declared variables: name -> {type, mutable}
  const symbols: { [k: string]: { type: string; mutable: boolean } } = {};

  for (const p of parts) {
    const s = p.trim();
    if (s.length === 0) continue;
    const stmt = s + ";"; // re-add semicolon for parsing convenience
    if (stmt.startsWith(letPrefix)) {
      const bodyRaw = stmt.substring(letPrefix.length, stmt.length - 1).trim();
      const decl = processDeclaration(bodyRaw, symbols);
      results.push(decl);
    } else if (stmt.startsWith('if')) {
      const ifDecl = compileIfStatement(stmt.substring(0, stmt.length - 1).trim(), symbols);
      results.push(ifDecl);
    } else {
      const assign = processAssignment(stmt, symbols);
      results.push(assign);
    }
  }
  return results;
}

function processDeclaration(bodyRaw: string, symbols: { [k: string]: { type: string; mutable: boolean } }): DeclResult {
  let isMutable = false;
  let body = bodyRaw;
  if (body.startsWith("mut ")) {
    isMutable = true;
    body = body.substring(4).trim();
  }
  const { name, typeName, value } = parseLetBody(body);
  if (!isValidIdentifier(name)) throw new Error("Invalid identifier");

  const decl = typeName ? compileTypedDeclaration(name, typeName, value) : compileUntypedDeclaration(name, value);
  symbols[name] = { type: decl.declaredType || (typeName || 'I32'), mutable: !!isMutable };
  return decl;
}

function processAssignment(stmt: string, symbols: { [k: string]: { type: string; mutable: boolean } }): DeclResult {
  const eqIndex = stmt.indexOf("=");
  if (eqIndex === -1) throw new Error("Invalid statement");
  const name = stmt.substring(0, eqIndex).trim();
  const value = stmt.substring(eqIndex + 1, stmt.length - 1).trim();
  if (!isValidIdentifier(name)) throw new Error("Invalid identifier");
  const sym = symbols[name];
  if (!sym) throw new Error("Assignment to undeclared variable");
  if (!sym.mutable) throw new Error("Assignment to immutable variable");

  const kindInfo = numericKind(value);
  const varType = sym.type;
  if ((varType[0] === 'I' || varType[0] === 'U')) return processIntegerAssignment(name, value, varType, kindInfo);
  if (varType === 'F32' || varType === 'F64') return processFloatAssignment(name, value, varType, kindInfo);
  if (varType === 'Bool') return processBooleanAssignment(name, value);
  throw new Error('Unsupported variable type for assignment');
}

function processIntegerAssignment(name: string, value: string, varType: string, kindInfo: { kind: string; suffix: string }): DeclResult {
  if (kindInfo.kind !== 'int') throw new Error('Type mismatch: expected integer literal');
  if (kindInfo.suffix.length !== 0 && kindInfo.suffix !== varType) throw new Error('Literal type suffix does not match declared type');
  let plainValue = value;
  if (kindInfo.suffix === varType) plainValue = value.substring(0, value.length - kindInfo.suffix.length);
  return { text: `${name} = ${plainValue};`, usesStdint: false, usesStdbool: false };
}

function processFloatAssignment(name: string, value: string, varType: string, kindInfo: { kind: string; suffix: string }): DeclResult {
  if (kindInfo.kind !== 'float') throw new Error('Type mismatch: expected floating literal');
  if (kindInfo.suffix.length !== 0 && kindInfo.suffix !== varType) throw new Error('Literal type suffix does not match declared type');
  let plainValue = value;
  if (kindInfo.suffix === varType) plainValue = value.substring(0, value.length - kindInfo.suffix.length);
  return { text: `${name} = ${plainValue};`, usesStdint: false, usesStdbool: false };
}

function processBooleanAssignment(name: string, value: string): DeclResult {
  // allow direct boolean literals
  if (value === 'true' || value === 'false') return { text: `${name} = ${value};`, usesStdint: false, usesStdbool: false };
  // or comparison expressions like `3 == 5`
  const cmp = tryParseComparison(value);
  if (cmp) return { text: `${name} = ${cmp.left} ${cmp.op} ${cmp.right};`, usesStdint: false, usesStdbool: false };
  throw new Error('Type mismatch: expected boolean literal or comparison');
}

function compileTypedDeclaration(name: string, typeName: string, value: string): DeclResult {
  if (supportedTypes.indexOf(typeName) === -1) throw new Error("Unsupported type");
  const info = getTypeInfo(typeName);
  if (info.kind === 'int' || info.kind === 'uint') return compileIntegerTyped(name, typeName, value);
  if (info.kind === 'float') return compileFloatTyped(name, typeName, value);
  if (info.kind === 'bool') return compileBooleanTyped(name, typeName, value);
  throw new Error("Unsupported type category");
}

function compileIntegerTyped(name: string, typeName: string, value: string): DeclResult {
  const kindInfo = numericKind(value);
  if (kindInfo.kind !== 'int') throw new Error('Type mismatch: expected integer literal');
  const suffix = kindInfo.suffix;
  if (suffix.length !== 0 && suffix !== typeName) throw new Error('Literal type suffix does not match declared type');
  let plainValue = value;
  if (suffix === typeName) {
    plainValue = value.substring(0, value.length - suffix.length);
  }
  const cType = typeMap[typeName] || typeName;
  return { text: `${cType} ${name} = ${plainValue};`, usesStdint: true, usesStdbool: false, declaredType: typeName };
}

function compileFloatTyped(name: string, typeName: string, value: string): DeclResult {
  const kindInfo = numericKind(value);
  if (kindInfo.kind !== 'float') throw new Error('Type mismatch: expected floating literal');
  if (kindInfo.suffix.length !== 0 && kindInfo.suffix !== typeName) throw new Error('Literal type suffix does not match declared type');
  let plainValue = value;
  if (kindInfo.suffix === typeName) {
    plainValue = value.substring(0, value.length - kindInfo.suffix.length);
  }
  const floatMap: { [k: string]: string } = { F32: "float", F64: "double" };
  return { text: `${floatMap[typeName]} ${name} = ${plainValue};`, usesStdint: false, usesStdbool: false, declaredType: typeName };
}

function compileBooleanTyped(name: string, typeName: string, value: string): DeclResult {
  const val = value.trim();
  // allow boolean literals
  if (val === 'true' || val === 'false') {
    const cType = typeMap[typeName] || 'bool';
    return { text: `${cType} ${name} = ${val};`, usesStdint: false, usesStdbool: true, declaredType: typeName };
  }
  // allow comparisons between numeric literals, e.g. `3 == 5`
  const cmp = tryParseComparison(val);
  if (cmp) {
    const cType = typeMap[typeName] || 'bool';
    return { text: `${cType} ${name} = ${cmp.left} ${cmp.op} ${cmp.right};`, usesStdint: false, usesStdbool: true, declaredType: typeName };
  }
  throw new Error('Type mismatch: expected boolean literal or comparison');
}

function findComparisonOp(s: string): { op: string; idx: number } | null {
  const operators = ['<=', '>=', '==', '!=', '<', '>'];
  for (const op of operators) {
    const idx = s.indexOf(op);
    if (idx !== -1) return { op, idx };
  }
  return null;
}

function validateAndStripNumericSides(leftRaw: string, rightRaw: string): { left: string; right: string } | null {
  if (leftRaw.length === 0 || rightRaw.length === 0) return null;
  const leftInfo = numericKind(leftRaw);
  const rightInfo = numericKind(rightRaw);
  if (leftInfo.kind === 'unknown' || rightInfo.kind === 'unknown') return null;
  ensureNumericCompatibility(leftInfo, rightInfo);
  const left = leftInfo.suffix.length !== 0 ? leftRaw.substring(0, leftRaw.length - leftInfo.suffix.length) : leftRaw;
  const right = rightInfo.suffix.length !== 0 ? rightRaw.substring(0, rightRaw.length - rightInfo.suffix.length) : rightRaw;
  return { left, right };
}

function ensureNumericCompatibility(leftInfo: { kind: string; suffix: string }, rightInfo: { kind: string; suffix: string }) {
  if (leftInfo.kind !== rightInfo.kind) throw new Error('Type mismatch in comparison');
  if (leftInfo.suffix.length !== 0 && rightInfo.suffix.length !== 0 && leftInfo.suffix !== rightInfo.suffix) {
    throw new Error('Mismatched literal suffixes in comparison');
  }
}

function tryParseComparison(expr: string): { left: string; op: string; right: string } | null {
  const s = expr.trim();
  const found = findComparisonOp(s);
  if (!found) return null;
  const { op, idx } = found;
  const leftRaw = s.substring(0, idx).trim();
  const rightRaw = s.substring(idx + op.length).trim();
  const stripped = validateAndStripNumericSides(leftRaw, rightRaw);
  if (!stripped) return null;
  return { left: stripped.left, op, right: stripped.right };
}

function compileUntypedDeclaration(name: string, value: string): DeclResult {
  let inferred = "I32";
  // boolean literal inference
  if (value === 'true' || value === 'false') {
    inferred = 'Bool';
    const outType = typeMap[inferred] || 'bool';
    return { text: `${outType} ${name} = ${value};`, usesStdint: false, usesStdbool: true, declaredType: inferred };
  }
  // handle float suffix/inference in helper to keep complexity low
  const floatInf = inferFloatSuffix(value);
  inferred = floatInf.inferred;
  value = floatInf.value;
  const outType = typeMap[inferred] || typeMap["I32"] || "int32_t";
  if (inferred[0] === 'I' || inferred[0] === 'U') {
    return { text: `${outType} ${name} = ${value};`, usesStdint: true, usesStdbool: false, declaredType: inferred };
  }
  return { text: `${outType} ${name} = ${value};`, usesStdint: false, usesStdbool: false, declaredType: inferred };
}

function inferFloatSuffix(value: string): { inferred: string; value: string } {
  let inferred = 'I32';
  const kind = numericKind(value);
  if (kind.kind === 'float') {
    if (kind.suffix.length !== 0) {
      if (kind.suffix === 'F32' || kind.suffix === 'F64') inferred = kind.suffix;
      else inferred = 'F32';
      value = value.substring(0, value.length - kind.suffix.length);
    } else {
      inferred = 'F32';
    }
  }
  return { inferred, value };
}

function compileFunction(src: string): DeclResult {
  // Expect form: fn name() : Type => { }
  const { name, params, afterParams } = parseFunctionHeader(src);
  const { returnType, body } = extractReturnAndBody(afterParams);
  const info = getTypeInfo(returnType);
  if (info.kind === 'other') throw new Error('Unsupported return type');
  const cReturn = info.cType;
  const { paramText, usesStdint } = buildParamInfo(params || []);
  const symbols = buildParamSymbols(params || []);
  const handlers: { [k: string]: (cReturn: string) => DeclResult } = {
    int: () => compileFunctionIntegerReturn(cReturn, name, returnType, body, paramText, usesStdint, symbols),
    uint: () => compileFunctionIntegerReturn(cReturn, name, returnType, body, paramText, usesStdint, symbols),
    bool: () => compileFunctionBoolReturn(cReturn, name, returnType, body, paramText, usesStdint, symbols),
    void: () => compileFunctionVoidReturn(cReturn, name, returnType, body, paramText, usesStdint, symbols),
    float: () => compileFunctionFloatReturn(cReturn, name, returnType, body, paramText, usesStdint, symbols),
  };
  const h = handlers[info.kind];
  if (!h) throw new Error('Unsupported return type for function');
  return h(cReturn);
}

function buildParamSymbols(params: Param[]): { [k: string]: { type: string; mutable: boolean } } {
  const symbols: { [k: string]: { type: string; mutable: boolean } } = {};
  if (!params || params.length === 0) return symbols;
  for (const p of params) symbols[p.name] = { type: p.type, mutable: false };
  return symbols;
}

type Param = { name: string; type: string };

function parseParams(src: string): { params: Param[]; restAfterParams: string } {
  if (!src.startsWith('(')) throw new Error('Invalid parameter list');
  const close = src.indexOf(')');
  if (close === -1) throw new Error('Unterminated parameter list');
  const inner = src.substring(1, close).trim();
  const restAfter = src.substring(close + 1).trim();
  if (inner === '') return { params: [], restAfterParams: restAfter };
  // Expect single parameter of form: name : Type
  const colon = inner.indexOf(':');
  if (colon === -1) throw new Error('Invalid parameter declaration');
  const pname = inner.substring(0, colon).trim();
  const ptype = inner.substring(colon + 1).trim();
  if (!isValidIdentifier(pname)) throw new Error('Invalid parameter name');
  return { params: [{ name: pname, type: ptype }], restAfterParams: restAfter };
}

function buildParamInfo(params: Param[]): { paramText: string; usesStdint: boolean } {
  if (!params || params.length === 0) return { paramText: '', usesStdint: false };
  const p = params[0];
  const info = getTypeInfo(p.type);
  if (info.kind === 'other') throw new Error('Unsupported parameter type');
  return { paramText: `${info.cType} ${p.name}`, usesStdint: info.usesStdint };
}

function parseFunctionHeader(src: string): { name: string; params: Param[]; afterParams: string } {
  if (!src.startsWith('fn ')) throw new Error('Invalid function declaration');
  const rest = src.substring(3).trim();
  const { name, restAfterName } = extractFunctionName(rest);
  if (!isValidIdentifier(name)) throw new Error('Invalid identifier');
  const { params, restAfterParams } = parseParams(restAfterName);
  return { name, params, afterParams: restAfterParams };
}

function stripBraces(body: string): string {
  let b = body.trim();
  if (b.startsWith('{')) b = b.substring(1);
  if (b.endsWith('}')) b = b.substring(0, b.length - 1);
  return b.trim();
}

function isIfStatement(src: string): boolean {
  return src.startsWith('if');
}

function findMatching(src: string, start: number, openChar: string, closeChar: string): number {
  let depth = 0;
  for (let i = start; i < src.length; i++) {
    const ch = src[i];
    if (ch === openChar) depth++;
    else if (ch === closeChar) {
      depth--;
      if (depth === 0) return i;
    }
  }
  return -1;
}

function compileIfStatement(src: string, symbols?: { [k: string]: { type: string; mutable: boolean } }): DeclResult {
  // Expect form: if(<cond>){<body>}
  // require parentheses and braces
  const afterIf = src.substring(2).trim();
  if (!afterIf.startsWith('(')) throw new Error('Invalid if: missing (');
  const closeParen = findMatching(afterIf, 0, '(', ')');
  if (closeParen === -1) throw new Error('Invalid if: unterminated (');
  const cond = afterIf.substring(1, closeParen).trim();
  const rest = afterIf.substring(closeParen + 1).trim();
  let body = '';
  if (rest.startsWith('{')) {
    const closeBrace = findMatching(rest, 0, '{', '}');
    if (closeBrace === -1) throw new Error('Invalid if: unterminated {');
    body = rest.substring(0, closeBrace + 1);
  } else {
    // allow single-statement form: if(cond) statement;
    // rest may or may not include a trailing semicolon depending on caller; normalize
    let stmt = rest;
    // if there's a semicolon in rest, take up to first semicolon
    const semi = stmt.indexOf(';');
    if (semi !== -1) {
      stmt = stmt.substring(0, semi).trim();
    } else {
      stmt = stmt.trim();
    }
    if (stmt.length === 0) throw new Error('Invalid if: empty statement');
    // normalize to brace form for emitted C
    const stmtWithSemi = stmt.endsWith(';') ? stmt : stmt + ';';
    body = `{${stmtWithSemi}}`;
  }
  // validate condition, allowing identifiers when provided via symbols
  const parsed = parseCondition(cond, symbols);
  if (!parsed.valid) throw new Error('Unsupported if condition');
  return { text: `if(${parsed.text})${body}`, usesStdint: parsed.usesStdint, usesStdbool: parsed.usesStdbool };
}
function parseSimpleBool(s: string) {
  if (s === 'true' || s === 'false') return { valid: true, text: s, usesStdint: false, usesStdbool: true };
  return null;
}

function parseIdentifierCondition(s: string, symbols?: { [k: string]: { type: string; mutable: boolean } }) {
  if (!isValidIdentifier(s) || !symbols) return null;
  const sym = symbols[s];
  if (!sym) return null;
  if (sym.type === 'Bool') return { valid: true, text: s, usesStdint: false, usesStdbool: true };
  return { valid: false, usesStdint: false, usesStdbool: false };
}

function sideInfo(side: string, symbols?: { [k: string]: { type: string; mutable: boolean } }) {
  if (isValidIdentifier(side)) {
    if (!symbols || !symbols[side]) return { kind: 'unknown' };
    const t = symbols[side].type;
    const info = getTypeInfo(t);
    return { kind: 'ident', text: side, cKind: info.kind, usesStdint: info.usesStdint };
  }
  const nk = numericKind(side);
  if (nk.kind === 'unknown') return { kind: 'unknown' };
  const text = nk.suffix.length !== 0 ? side.substring(0, side.length - nk.suffix.length) : side;
  return { kind: 'literal', text, cKind: nk.kind };
}

function parseComparisonCondition(s: string, symbols?: { [k: string]: { type: string; mutable: boolean } }) {
  const found = findComparisonOp(s);
  if (!found) return null;
  const { op, idx } = found;
  const leftRaw = s.substring(0, idx).trim();
  const rightRaw = s.substring(idx + op.length).trim();
  const L = sideInfo(leftRaw, symbols);
  const R = sideInfo(rightRaw, symbols);
  if (L.kind === 'unknown' || R.kind === 'unknown') return null;
  const leftKind = L.cKind;
  const rightKind = R.cKind;
  compareKindsCompatible(leftKind, rightKind);
  const usesStdint = !!(L.usesStdint || R.usesStdint);
  const leftText = L.text || leftRaw;
  const rightText = R.text || rightRaw;
  return { valid: true, text: `${leftText} ${op} ${rightText}`, usesStdint, usesStdbool: false };
}

function compareKindsCompatible(leftKind: string | undefined, rightKind: string | undefined) {
  if (leftKind === rightKind) return;
  // allow int compared to uint (or vice versa) as compatible
  if ((leftKind === 'int' && (rightKind === 'int' || rightKind === 'uint')) || (rightKind === 'int' && (leftKind === 'int' || leftKind === 'uint'))) return;
  throw new Error('Type mismatch in comparison');
}

function parseCondition(cond: string, symbols?: { [k: string]: { type: string; mutable: boolean } }): { valid: boolean; text?: string; usesStdint: boolean; usesStdbool: boolean } {
  const s = cond.trim();
  const simple = parseSimpleBool(s);
  if (simple) return simple;
  const id = parseIdentifierCondition(s, symbols);
  if (id) return id;
  const cmp = parseComparisonCondition(s, symbols);
  if (cmp) return cmp;
  return { valid: false, usesStdint: false, usesStdbool: false };
}

function extractReturnLiteral(inner: string): string {
  if (!inner.startsWith('return')) throw new Error('Missing return');
  const rest = inner.substring('return'.length).trim();
  if (!rest.endsWith(';')) throw new Error('Invalid return');
  return rest.substring(0, rest.length - 1).trim();
}

function validateIntegerLiteral(lit: string) {
  if (lit.length === 0) throw new Error('Empty return literal');
  let idx = 0;
  if (lit[0] === '+' || lit[0] === '-') idx = 1;
  for (; idx < lit.length; idx++) {
    const ch = lit[idx];
    if (!isDigit(ch)) throw new Error('Only integer literals supported');
  }
}

function validateFloatLiteral(lit: string) {
  if (lit.length === 0) throw new Error('Empty return literal');
  if (lit.indexOf('.') === -1) throw new Error('Only float literals supported');
}

function validateBoolLiteral(lit: string) {
  if (lit !== 'true' && lit !== 'false') throw new Error('Only simple boolean return statements supported');
}

function parseIfReturnBody(inner: string, kind: 'int' | 'float' | 'bool', symbols?: { [k: string]: { type: string; mutable: boolean } }): { condParsed: { valid: boolean; text?: string; usesStdint: boolean; usesStdbool: boolean }; literal: string } {
  if (!inner.startsWith('if')) throw new Error('Only simple if-return supported');
  const afterIf = inner.substring(2).trim();
  const closeParen = findMatching(afterIf, 0, '(', ')');
  if (closeParen === -1) throw new Error('Only simple if-return supported');
  const cond = afterIf.substring(1, closeParen).trim();
  const rest = afterIf.substring(closeParen + 1).trim();
  const innerBody = getIfInnerBody(rest);
  const lit = extractReturnLiteral(innerBody);
  validateReturnExpression(kind, lit);
  const condParsed = parseCondition(cond, symbols);
  if (!condParsed.valid) throw new Error('Unsupported if condition');
  return { condParsed, literal: lit };
}

function getIfInnerBody(rest: string): string {
  if (rest.startsWith('{')) {
    const closeBrace = findMatching(rest, 0, '{', '}');
    if (closeBrace === -1) throw new Error('Only simple if-return supported');
    return rest.substring(1, closeBrace).trim();
  }
  const semi = rest.indexOf(';');
  if (semi === -1) throw new Error('Only simple if-return supported');
  return rest.substring(0, semi + 1).trim();
}

function validateReturnExpression(kind: 'int' | 'float' | 'bool', lit: string) {
  if (isValidIdentifier(lit)) return;
  if (kind === 'int') return validateIntegerLiteral(lit);
  if (kind === 'float') return validateFloatLiteral(lit);
  if (kind === 'bool') return validateBoolLiteral(lit);
}

function compileFunctionIntegerReturn(cReturn: string, name: string, returnType: string, body: string, paramText: string, paramUsesStdint: boolean, symbols?: { [k: string]: { type: string; mutable: boolean } }): DeclResult {
  const inner = stripBraces(body);
  // direct return
  if (inner.startsWith('return')) {
    const lit = extractReturnLiteral(inner);
    validateIntegerLiteral(lit);
    const params = paramText ? paramText : '';
    return { text: `${cReturn} ${name}(${params}){return ${lit};}`, usesStdint: true || paramUsesStdint, usesStdbool: false, declaredType: returnType };
  }
  // if-return
  // allow preceding let statements: e.g. `let x : I32 = 0; return 1;` or `let x : I32 = 0; if(cond) return 1;`
  const preludeAttempt = tryCompileFunctionWithPrelude('int', cReturn, name, returnType, inner, paramText, paramUsesStdint, symbols);
  if (preludeAttempt) return preludeAttempt;
  const parsed = parseIfReturnBody(inner, 'int', symbols);
  const params = paramText ? paramText : '';
  return { text: `${cReturn} ${name}(${params}){if(${parsed.condParsed.text}){return ${parsed.literal};}}`, usesStdint: true || paramUsesStdint || parsed.condParsed.usesStdint, usesStdbool: parsed.condParsed.usesStdbool, declaredType: returnType };
}

function compileFunctionBoolReturn(cReturn: string, name: string, returnType: string, body: string, paramText: string, paramUsesStdint: boolean, symbols?: { [k: string]: { type: string; mutable: boolean } }): DeclResult {
  const inner = stripBraces(body);
  if (inner.startsWith('return')) {
    const lit = extractReturnLiteral(inner);
    validateBoolLiteral(lit);
    const params = paramText ? paramText : '';
    return { text: `${cReturn} ${name}(${params}){return ${lit};}`, usesStdint: paramUsesStdint, usesStdbool: false, declaredType: returnType };
  }
  const preludeBool = tryCompileFunctionWithPrelude('bool', cReturn, name, returnType, inner, paramText, paramUsesStdint, symbols);
  if (preludeBool) return preludeBool;
  const parsed = parseIfReturnBody(inner, 'bool', symbols);
  const params = paramText ? paramText : '';
  return { text: `${cReturn} ${name}(${params}){if(${parsed.condParsed.text}){return ${parsed.literal};}}`, usesStdint: paramUsesStdint || parsed.condParsed.usesStdint, usesStdbool: parsed.condParsed.usesStdbool, declaredType: returnType };
}

function compileFunctionVoidReturn(cReturn: string, name: string, returnType: string, body: string, paramText: string, paramUsesStdint: boolean, symbols?: { [k: string]: { type: string; mutable: boolean } }): DeclResult {
  const inner = stripBraces(body);
  // allow either empty body or a single if statement
  if (inner === '') {
    const params = paramText ? paramText : '';
    return { text: `${cReturn} ${name}(${params}){}`, usesStdint: paramUsesStdint, usesStdbool: false, declaredType: returnType };
  }
  // allow one or more inner statements (lets, ifs, assignments)
  const trimmed = inner.trim();
  const parts = splitTopLevelStatements(trimmed);
  if (parts.length === 0) throw new Error('Invalid function body');
  const compiled = compileInnerStmtList(parts, symbols);
  const params = paramText ? paramText : '';
  return { text: `${cReturn} ${name}(${params}){${compiled.text}}`, usesStdint: paramUsesStdint || compiled.usesStdint, usesStdbool: compiled.usesStdbool, declaredType: returnType };
}

function compileFunctionFloatReturn(cReturn: string, name: string, returnType: string, body: string, paramText: string, paramUsesStdint: boolean, symbols?: { [k: string]: { type: string; mutable: boolean } }): DeclResult {
  const inner = stripBraces(body);
  if (inner.startsWith('return')) {
    const lit = extractReturnLiteral(inner);
    validateFloatLiteral(lit);
    const params = paramText ? paramText : '';
    return { text: `${cReturn} ${name}(${params}){return ${lit};}`, usesStdint: paramUsesStdint, usesStdbool: false, declaredType: returnType };
  }
  const preludeFloat = tryCompileFunctionWithPrelude('float', cReturn, name, returnType, inner, paramText, paramUsesStdint, symbols);
  if (preludeFloat) return preludeFloat;
  const parsed = parseIfReturnBody(inner, 'float', symbols);
  const params = paramText ? paramText : '';
  return { text: `${cReturn} ${name}(${params}){if(${parsed.condParsed.text}){return ${parsed.literal};}}`, usesStdint: paramUsesStdint || parsed.condParsed.usesStdint, usesStdbool: parsed.condParsed.usesStdbool, declaredType: returnType };
}

function compileInnerStmtList(parts: string[], symbols?: { [k: string]: { type: string; mutable: boolean } }): { text: string; usesStdint: boolean; usesStdbool: boolean } {
  if (!parts || parts.length === 0) return { text: '', usesStdint: false, usesStdbool: false };
  const texts: string[] = [];
  let usesStdint = false;
  let usesStdbool = false;
  for (const p of parts) {
    const res = compileSingleInnerStmt(p, symbols);
    texts.push(res.text);
    usesStdint = usesStdint || res.usesStdint;
    usesStdbool = usesStdbool || res.usesStdbool;
  }
  const joined = texts.join('');
  return { text: joined, usesStdint, usesStdbool };
}

function splitTopLevelStatements(src: string): string[] {
  const out: string[] = [];
  let i = 0;
  const n = src.length;
  while (i < n) {
    while (i < n && /\s/.test(src[i])) i++;
    if (i >= n) break;
    if (src.startsWith('if', i)) {
      const parsedIf = parseIfSegment(src, i);
      out.push(parsedIf.stmt);
      i = parsedIf.nextIndex;
      continue;
    }
    const parsed = parseUntilSemicolon(src, i);
    out.push(parsed.stmt);
    i = parsed.nextIndex;
  }
  return out;
}

function tryCompileFunctionWithPrelude(kind: 'int' | 'float' | 'bool', cReturn: string, name: string, returnType: string, inner: string, paramText: string, paramUsesStdint: boolean, symbols?: { [k: string]: { type: string; mutable: boolean } }): DeclResult | null {
  const trimmed = inner.trim();
  if (!(trimmed.startsWith('let') || trimmed.indexOf(';') !== -1)) return null;
  const parts = splitTopLevelStatements(trimmed);
  if (parts.length === 0) return null;
  const last = parts[parts.length - 1];
  const before = parts.slice(0, parts.length - 1);
  const beforeCompiled = compileInnerStmtList(before, symbols);
  const params = paramText ? paramText : '';
  if (last.startsWith('return')) return tryPreludeReturn(kind, cReturn, name, returnType, params, paramUsesStdint, beforeCompiled, last);
  if (last.startsWith('if')) return tryPreludeIf(kind, cReturn, name, returnType, params, paramUsesStdint, beforeCompiled, last, symbols);
  return null;
}

function tryPreludeReturn(kind: 'int' | 'float' | 'bool', cReturn: string, name: string, returnType: string, params: string, paramUsesStdint: boolean, beforeCompiled: { text: string; usesStdint: boolean; usesStdbool: boolean }, last: string): DeclResult | null {
  const lit = extractReturnLiteral(last);
  // allow identifiers as return expressions as well as literals
  if (!isValidIdentifier(lit)) {
    if (kind === 'int') validateIntegerLiteral(lit);
    if (kind === 'float') validateFloatLiteral(lit);
    if (kind === 'bool') validateBoolLiteral(lit);
  }
  const bodyText = beforeCompiled.text + `return ${lit};`;
  return { text: `${cReturn} ${name}(${params}){${bodyText}}`, usesStdint: paramUsesStdint || beforeCompiled.usesStdint || (kind === 'int'), usesStdbool: beforeCompiled.usesStdbool, declaredType: returnType };
}

function tryPreludeIf(kind: 'int' | 'float' | 'bool', cReturn: string, name: string, returnType: string, params: string, paramUsesStdint: boolean, beforeCompiled: { text: string; usesStdint: boolean; usesStdbool: boolean }, last: string, symbols?: { [k: string]: { type: string; mutable: boolean } }): DeclResult | null {
  const parsed = parseIfReturnBody(last, kind, symbols);
  const bodyText = beforeCompiled.text + `if(${parsed.condParsed.text}){return ${parsed.literal};}`;
  return { text: `${cReturn} ${name}(${params}){${bodyText}}`, usesStdint: paramUsesStdint || beforeCompiled.usesStdint || parsed.condParsed.usesStdint || (kind === 'int'), usesStdbool: beforeCompiled.usesStdbool || parsed.condParsed.usesStdbool, declaredType: returnType };
}

function compileSingleInnerStmt(p: string, symbols?: { [k: string]: { type: string; mutable: boolean } }): { text: string; usesStdint: boolean; usesStdbool: boolean } {
  if (p.startsWith('let')) {
    let bodyRaw = p.substring(3).trim();
    if (bodyRaw.endsWith(';')) bodyRaw = bodyRaw.substring(0, bodyRaw.length - 1).trim();
    const decl = processDeclaration(bodyRaw, symbols || {});
    return { text: decl.text, usesStdint: decl.usesStdint, usesStdbool: decl.usesStdbool };
  }
  if (p.startsWith('if')) {
    const ifRes = compileIfStatement(p, symbols);
    return { text: ifRes.text, usesStdint: ifRes.usesStdint, usesStdbool: ifRes.usesStdbool };
  }
  const assign = processAssignment(p.endsWith(';') ? p : p + ';', symbols || {});
  return { text: assign.text, usesStdint: assign.usesStdint, usesStdbool: assign.usesStdbool };
}

function findIfRange(src: string, i: number): { start: number; end: number } {
  const n = src.length;
  const k = locateIfBodyStart(src, i);
  // k now points at first non-space after closeParen
  if (k < n && src[k] === '{') {
    const closeBrace = findMatching(src, k, '{', '}');
    if (closeBrace === -1) throw new Error('Unterminated { in inner if');
    return { start: i, end: closeBrace + 1 };
  }
  const semi = src.indexOf(';', k);
  if (semi === -1) throw new Error('Missing ; after inner if single-statement');
  return { start: i, end: semi + 1 };
}

function locateIfBodyStart(src: string, i: number): number {
  const n = src.length;
  let j = i + 2;
  while (j < n && /\s/.test(src[j])) j++;
  if (j >= n || src[j] !== '(') throw new Error('Invalid if syntax in inner statements');
  const closeParen = findMatching(src, j, '(', ')');
  if (closeParen === -1) throw new Error('Unterminated ( in inner if');
  let k = closeParen + 1;
  while (k < n && /\s/.test(src[k])) k++;
  return k;
}

function parseIfSegment(src: string, i: number): { stmt: string; nextIndex: number } {
  const r = findIfRange(src, i);
  return { stmt: src.substring(r.start, r.end).trim(), nextIndex: r.end };
}

function parseUntilSemicolon(src: string, i: number): { stmt: string; nextIndex: number } {
  const semi = src.indexOf(';', i);
  if (semi === -1) return { stmt: src.substring(i).trim(), nextIndex: src.length };
  return { stmt: src.substring(i, semi + 1).trim(), nextIndex: semi + 1 };
}

// helper removed (inlined) to satisfy lint complexity/unused rules

function extractFunctionName(src: string): { name: string; restAfterName: string } {
  const parenIndex = src.indexOf('(');
  if (parenIndex === -1) throw new Error('Invalid function declaration');
  const name = src.substring(0, parenIndex).trim();
  const restAfterName = src.substring(parenIndex);
  return { name, restAfterName };
}

// validateEmptyParams removed - parseParams handles parameter lists

function extractReturnAndBody(src: string): { returnType: string; body: string } {
  if (!src.startsWith(':')) throw new Error('Missing return type');
  const rest = src.substring(1).trim();
  const arrowIndex = rest.indexOf('=>');
  if (arrowIndex === -1) throw new Error('Invalid function declaration');
  const returnType = rest.substring(0, arrowIndex).trim();
  const body = rest.substring(arrowIndex + 2).trim();
  return { returnType, body };
}

