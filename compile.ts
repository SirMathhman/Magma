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

function isCharLiteral(s: string): boolean {
  // accept single char like 'a' or escaped like '\n' or '\''
  if (!s || s.length < 3) return false;
  if (s[0] !== "'" || s[s.length - 1] !== "'") return false;
  const inner = s.substring(1, s.length - 1);
  // allow single character or an escape sequence like \n or \' or \\
  if (inner.length === 1) return true;
  if (inner.length === 2 && inner[0] === '\\') return true;
  return false;
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
  // support single or multiple top-level import statements separated by semicolons
  // e.g. `import first; import second;` -> emits `#include <first.h>` and `#include <second.h>`
  if (!src.startsWith('import ')) return null;
  const parts = splitTopLevelBySemicolon(src);
  const includes: string[] = [];
  const seen = new Set<string>();
  for (const p of parts) {
    const s = p.trim();
    if (s.length === 0) continue;
    if (!s.startsWith('import ')) return null;
    let rest = s.substring('import '.length).trim();
    // strip any trailing semicolon leftover
    if (rest.endsWith(';')) rest = rest.substring(0, rest.length - 1).trim();
    if (!isValidIdentifier(rest)) throw new Error('Invalid import name: ' + rest);
    if (seen.has(rest)) throw new Error('Duplicate import: ' + rest);
    seen.add(rest);
    includes.push(`#include <${rest}.h>`);
  }
  if (includes.length === 0) return '';
  // emit each include on its own CRLF terminated line in the order declared
  return includes.map(i => i + '\r\n').join('');
}

function compileStatements(src: string): DeclResult[] {
  const letPrefix = "let ";
  const parts = splitTopLevelBySemicolon(src);
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

function splitTopLevelBySemicolon(src: string): string[] {
  const out: string[] = [];
  let cur = '';
  let depthParen = 0;
  let depthBrace = 0;
  let depthBracket = 0;
  for (let i = 0; i < src.length; i++) {
    const ch = src[i];
    const delta = updateDepths(ch);
    depthParen += delta.parenDelta;
    depthBrace += delta.braceDelta;
    depthBracket += delta.bracketDelta;
    if (ch === ';' && depthParen === 0 && depthBrace === 0 && depthBracket === 0) {
      out.push(cur);
      cur = '';
      continue;
    }
    cur += ch;
  }
  if (cur.trim().length > 0) out.push(cur);
  return out;
}

function updateDepths(ch: string): { parenDelta: number; braceDelta: number; bracketDelta: number } {
  // returns delta adjustments for nesting depths for a single character
  if (ch === '(') return { parenDelta: 1, braceDelta: 0, bracketDelta: 0 };
  if (ch === ')') return { parenDelta: -1, braceDelta: 0, bracketDelta: 0 };
  if (ch === '{') return { parenDelta: 0, braceDelta: 1, bracketDelta: 0 };
  if (ch === '}') return { parenDelta: 0, braceDelta: -1, bracketDelta: 0 };
  if (ch === '[') return { parenDelta: 0, braceDelta: 0, bracketDelta: 1 };
  if (ch === ']') return { parenDelta: 0, braceDelta: 0, bracketDelta: -1 };
  return { parenDelta: 0, braceDelta: 0, bracketDelta: 0 };
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
  const decl = typeName ? compileTypedDeclaration(name, typeName, value, symbols) : compileUntypedDeclaration(name, value);
  symbols[name] = { type: decl.declaredType || (typeName || 'I32'), mutable: !!isMutable };
  return decl;
}

function processAssignment(stmt: string, symbols: { [k: string]: { type: string; mutable: boolean } }): DeclResult {
  const eqIndex = stmt.indexOf("=");
  if (eqIndex === -1) throw new Error("Invalid statement");
  const name = stmt.substring(0, eqIndex).trim();
  const value = stmt.substring(eqIndex + 1, stmt.length - 1).trim();
  const { sym, varName } = validateAssignmentTarget(name, symbols);
  const kindInfo = numericKind(value);
  const varType = sym.type;
  const idxRhs = tryHandleIndexAssignment(value);
  if (idxRhs) return { text: `${varName} = ${idxRhs};`, usesStdint: false, usesStdbool: false };
  if ((varType[0] === 'I' || varType[0] === 'U')) return processIntegerAssignment(varName, value, varType, kindInfo);
  if (varType === 'F32' || varType === 'F64') return processFloatAssignment(varName, value, varType, kindInfo);
  if (varType === 'Bool') return processBooleanAssignment(varName, value, symbols);
  throw new Error('Unsupported variable type for assignment');
}

function validateAssignmentTarget(name: string, symbols: { [k: string]: { type: string; mutable: boolean } }): { sym: { type: string; mutable: boolean }; varName: string } {
  if (!isValidIdentifier(name)) throw new Error("Invalid identifier");
  const sym = symbols[name];
  if (!sym) throw new Error("Assignment to undeclared variable");
  if (!sym.mutable) throw new Error("Assignment to immutable variable");
  return { sym, varName: name };
}

function tryHandleIndexAssignment(value: string): string | null {
  const idxMatch = value.match(/^([A-Za-z_][A-Za-z0-9_]*)\[(\d+)\]$/);
  if (!idxMatch) return null;
  const arrName = idxMatch[1];
  const idx = idxMatch[2];
  return `${arrName}[${idx}]`;
}

function processIntegerAssignment(name: string, value: string, varType: string, kindInfo: { kind: string; suffix: string }): DeclResult {
  // allow char literal as integer assignment
  if (isCharLiteral(value)) {
    const code = value.charCodeAt(1);
    return { text: `${name} = ${code};`, usesStdint: true, usesStdbool: false };
  }
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

function processBooleanAssignment(name: string, value: string, symbols?: { [k: string]: { type: string; mutable: boolean } }): DeclResult {
  // allow direct boolean literals
  if (value === 'true' || value === 'false') return { text: `${name} = ${value};`, usesStdint: false, usesStdbool: false };
  // or comparison expressions like `3 == 5`
  const cmp = tryParseComparison(value);
  if (cmp) return { text: `${name} = ${cmp.left} ${cmp.op} ${cmp.right};`, usesStdint: false, usesStdbool: false };
  // or logical expressions (&&, ||) / parenthesized combinations
  const parsed = parseCondition(value, symbols);
  if (parsed.valid) return { text: `${name} = ${parsed.text};`, usesStdint: parsed.usesStdint, usesStdbool: parsed.usesStdbool };
  throw new Error('Type mismatch: expected boolean literal or comparison');
}

function compileTypedDeclaration(name: string, typeName: string, value: string, symbols?: { [k: string]: { type: string; mutable: boolean } }): DeclResult {
  // support array types of form: [T; N]
  if (typeName.startsWith('[')) return compileArrayTyped(name, typeName, value);
  // pointer types like *CStr
  if (typeName === '*CStr') return compilePointerTyped(name, typeName, value);
  if (supportedTypes.indexOf(typeName) === -1) throw new Error("Unsupported type");
  const info = getTypeInfo(typeName);
  if (info.kind === 'int' || info.kind === 'uint') return compileIntegerTyped(name, typeName, value);
  if (info.kind === 'float') return compileFloatTyped(name, typeName, value);
  if (info.kind === 'bool') return compileBooleanTyped(name, typeName, value, symbols);
  throw new Error("Unsupported type category");
}

function compilePointerTyped(name: string, typeName: string, value: string): DeclResult {
  const v = value.trim();
  // only allow string literals for *CStr for now
  if (!(v.length >= 2 && v[0] === '"' && v[v.length - 1] === '"')) throw new Error('Type mismatch: expected string literal');
  // emit const char* for C strings
  const cType = 'const ' + (typeMap[typeName] || typeName);
  return { text: `${cType} ${name} = ${v};`, usesStdint: false, usesStdbool: false, declaredType: typeName };
}

function parseArrayType(typeName: string): { elemType: string; size: number } {
  // expect format: [ElemType; Size]
  const t = typeName.trim();
  if (!t.startsWith('[') || !t.endsWith(']')) throw new Error('Invalid array type');
  const inner = t.substring(1, t.length - 1).trim();
  const semi = inner.indexOf(';');
  if (semi === -1) throw new Error('Invalid array type');
  const elem = inner.substring(0, semi).trim();
  const sizeStr = inner.substring(semi + 1).trim();
  if (elem.length === 0 || sizeStr.length === 0) throw new Error('Invalid array type');
  // size must be a decimal integer literal
  for (let i = 0; i < sizeStr.length; i++) {
    const ch = sizeStr[i];
    if (ch < '0' || ch > '9') throw new Error('Invalid array size');
  }
  const size = parseInt(sizeStr, 10);
  return { elemType: elem, size };
}

function compileArrayTyped(name: string, typeName: string, value: string): DeclResult {
  const parsed = parseArrayType(typeName);
  const elemType = parsed.elemType;
  const size = parsed.size;
  if (supportedTypes.indexOf(elemType) === -1) throw new Error('Unsupported element type for array');
  const elems = parseArrayLiteral(value);
  if (elems.length !== size) throw new Error('Array literal length does not match declared size');
  const info = getTypeInfo(elemType);
  const plainElems = elems.map(e => elementToPlain(e, elemType, info));
  const cType = typeMap[elemType] || elemType;
  return { text: `${cType} ${name}[${size}] = {${plainElems.join(', ')}};`, usesStdint: info.usesStdint, usesStdbool: info.usesStdbool, declaredType: typeName };
}

function parseArrayLiteral(value: string): string[] {
  const v = value.trim();
  if (!v.startsWith('[') || !v.endsWith(']')) throw new Error('Invalid array literal');
  const inner = v.substring(1, v.length - 1).trim();
  if (inner.length === 0) return [];
  return inner.split(',').map(s => s.trim()).filter(s => s.length > 0);
}

function elementToPlain(e: string, elemType: string, info: { cType: string; usesStdint: boolean; usesStdbool: boolean; kind: string }): string {
  if (info.kind === 'int' || info.kind === 'uint') return elementIntPlain(e, elemType);
  if (info.kind === 'float') return elementFloatPlain(e, elemType);
  if (info.kind === 'bool') return elementBoolPlain(e);
  throw new Error('Unsupported array element type');
}

function elementIntPlain(e: string, elemType: string): string {
  const nk = numericKind(e);
  if (nk.kind !== 'int') throw new Error('Array element type mismatch: expected integer literal');
  if (nk.suffix.length !== 0 && nk.suffix !== elemType) throw new Error('Array element literal suffix does not match element type');
  return nk.suffix === elemType ? e.substring(0, e.length - nk.suffix.length) : e;
}

function elementFloatPlain(e: string, elemType: string): string {
  const nk = numericKind(e);
  if (nk.kind !== 'float') throw new Error('Array element type mismatch: expected float literal');
  if (nk.suffix.length !== 0 && nk.suffix !== elemType) throw new Error('Array element literal suffix does not match element type');
  return nk.suffix === elemType ? e.substring(0, e.length - nk.suffix.length) : e;
}

function elementBoolPlain(e: string): string {
  if (e !== 'true' && e !== 'false') throw new Error('Array element type mismatch: expected boolean literal');
  return e;
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

function compileBooleanTyped(name: string, typeName: string, value: string, symbols?: { [k: string]: { type: string; mutable: boolean } }): DeclResult {
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
  // allow logical expressions (&&, ||) and parenthesized combinations
  const parsed = parseCondition(val, symbols);
  if (parsed.valid) {
    const cType = typeMap[typeName] || 'bool';
    return { text: `${cType} ${name} = ${parsed.text};`, usesStdint: parsed.usesStdint, usesStdbool: parsed.usesStdbool, declaredType: typeName };
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
  // require same numeric kind (int, uint, float)
  if (leftInfo.kind !== rightInfo.kind) throw new Error('Type mismatch in comparison: mismatched numeric kinds');
  // if both have explicit suffixes, they must match
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
  // try special untyped handlers first
  const charHandled = tryHandleCharUntyped(name, value);
  if (charHandled) return charHandled;
  const arrHandled = tryHandleArrayUntyped(name, value);
  if (arrHandled) return arrHandled;
  let inferred = "I32";
  // char literal inference -> treat as U8
  if (isCharLiteral(value)) {
    inferred = 'U8';
    const outType = typeMap[inferred] || 'uint8_t';
    return { text: `${outType} ${name} = ${value.charCodeAt(1)};`, usesStdint: true, usesStdbool: false, declaredType: inferred };
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

function tryHandleCharUntyped(name: string, value: string): DeclResult | null {
  if (!isCharLiteral(value)) return null;
  const inferred = 'U8';
  const outType = typeMap[inferred] || 'uint8_t';
  return { text: `${outType} ${name} = ${value.charCodeAt(1)};`, usesStdint: true, usesStdbool: false, declaredType: inferred };
}

function tryHandleArrayUntyped(name: string, value: string): DeclResult | null {
  const v = value.trim();
  if (!v.startsWith('[') || !v.endsWith(']')) return null;
  const elems = parseArrayLiteral(value);
  if (elems.length === 0) throw new Error('Cannot infer type for empty array');
  const inferred = inferArrayElements(elems);
  const elemTypeName = inferred.typeName;
  const plainElems = inferred.elems;
  const elemCType = typeMap[elemTypeName] || elemTypeName;
  const usesStdint = (elemTypeName[0] === 'I' || elemTypeName[0] === 'U');
  return { text: `${elemCType} ${name}[${plainElems.length}] = {${plainElems.join(', ')}};`, usesStdint, usesStdbool: (elemTypeName === 'Bool'), declaredType: `[${elemTypeName}; ${plainElems.length}]` };
}

function inferArrayElements(elems: string[]): { typeName: string; elems: string[] } {
  let elemTypeName: string | null = null;
  const plainElems: string[] = [];
  for (const e of elems) {
    const res = processArrayElement(e, elemTypeName);
    elemTypeName = res.typeName;
    plainElems.push(res.plain);
  }
  if (!elemTypeName) throw new Error('Could not infer element type');
  return { typeName: elemTypeName, elems: plainElems };
}

function processArrayElement(e: string, currentType: string | null): { typeName: string; plain: string } {
  if (isCharLiteral(e)) return processArrayElementChar(e, currentType);
  if (e === 'true' || e === 'false') return processArrayElementBool(e, currentType);
  const nk = numericKind(e);
  if (nk.kind === 'unknown') throw new Error('Unsupported array element');
  if (nk.kind === 'float') return processArrayElementFloat(e, nk, currentType);
  return processArrayElementInt(e, nk, currentType);
}

function processArrayElementChar(e: string, currentType: string | null) {
  if (!currentType) currentType = 'U8';
  if (currentType !== 'U8') throw new Error('Mixed element types in array');
  return { typeName: currentType, plain: String(e.charCodeAt(1)) };
}

function processArrayElementBool(e: string, currentType: string | null) {
  if (!currentType) currentType = 'Bool';
  if (currentType !== 'Bool') throw new Error('Mixed element types in array');
  return { typeName: currentType, plain: e };
}

function processArrayElementFloat(e: string, nk: { kind: string; suffix: string }, currentType: string | null) {
  const tname = nk.suffix.length !== 0 ? nk.suffix : 'F32';
  if (!currentType) currentType = tname;
  if (currentType !== tname) throw new Error('Mixed element types in array');
  const plain = nk.suffix === tname ? e.substring(0, e.length - nk.suffix.length) : e;
  return { typeName: currentType, plain };
}

function processArrayElementInt(e: string, nk: { kind: string; suffix: string }, currentType: string | null) {
  const tname = nk.suffix.length !== 0 ? nk.suffix : 'I32';
  if (!currentType) currentType = tname;
  if (currentType !== tname) throw new Error('Mixed element types in array');
  const plain = nk.suffix === tname ? e.substring(0, e.length - nk.suffix.length) : e;
  return { typeName: currentType, plain };
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
  if (!leftKind || !rightKind) throw new Error('Type mismatch in comparison');
  const leftIsNumeric = leftKind === 'int' || leftKind === 'uint' || leftKind === 'float';
  const rightIsNumeric = rightKind === 'int' || rightKind === 'uint' || rightKind === 'float';
  if (!leftIsNumeric || !rightIsNumeric) throw new Error('Type mismatch in comparison: non-numeric operands');
  if (leftKind !== rightKind) throw new Error('Type mismatch in comparison: operand kinds differ');
}

function parseCondition(cond: string, symbols?: { [k: string]: { type: string; mutable: boolean } }): { valid: boolean; text?: string; usesStdint: boolean; usesStdbool: boolean } {
  const s = cond.trim();
  // First attempt to parse logical expressions (|| and &&) respecting precedence
  const logical = parseLogicalCondition(s, symbols);
  if (logical) return logical;
  return { valid: false, usesStdint: false, usesStdbool: false };
}

function splitTopLevelByOp(s: string, op: string): string[] {
  const parts: string[] = [];
  let cur = '';
  let depth = 0;
  for (let i = 0; i < s.length; i++) {
    const ch = s[i];
    if (ch === '(') depth++;
    else if (ch === ')') depth--;
    // check for op at this position when at top-level
    if (depth === 0 && s.substring(i, i + op.length) === op) {
      parts.push(cur);
      cur = '';
      i += op.length - 1;
      continue;
    }
    cur += ch;
  }
  parts.push(cur);
  return parts.map(p => p.trim()).filter(p => p.length > 0);
}

function parseLogicalCondition(s: string, symbols?: { [k: string]: { type: string; mutable: boolean } }): { valid: boolean; text?: string; usesStdint: boolean; usesStdbool: boolean } | null {
  // handle || lowest precedence
  const orParts = splitTopLevelByOp(s, '||');
  if (orParts.length > 1) {
    const texts: string[] = [];
    let usesStdint = false;
    for (const part of orParts) {
      const andParsed = parseAndCondition(part, symbols);
      if (!andParsed || !andParsed.valid) return null;
      texts.push(andParsed.text as string);
      usesStdint = usesStdint || andParsed.usesStdint;
    }
    return { valid: true, text: texts.join(' || '), usesStdint, usesStdbool: true };
  }
  // handle && only
  const andParsed = parseAndCondition(s, symbols);
  if (andParsed && andParsed.valid) return andParsed;
  return null;
}

function parseAndCondition(s: string, symbols?: { [k: string]: { type: string; mutable: boolean } }): { valid: boolean; text?: string; usesStdint: boolean; usesStdbool: boolean } | null {
  const andParts = splitTopLevelByOp(s, '&&');
  if (andParts.length > 1) {
    const texts: string[] = [];
    let usesStdint = false;
    for (const part of andParts) {
      const atom = parseAtomicBoolean(part, symbols);
      if (!atom || !atom.valid) return null;
      texts.push(atom.text as string);
      usesStdint = usesStdint || atom.usesStdint;
    }
    return { valid: true, text: texts.join(' && '), usesStdint, usesStdbool: true };
  }
  return parseAtomicBoolean(s, symbols);
}

function parseAtomicBoolean(s: string, symbols?: { [k: string]: { type: string; mutable: boolean } }): { valid: boolean; text?: string; usesStdint: boolean; usesStdbool: boolean } | null {
  const t = s.trim();
  if (t.length === 0) return null;
  const par = tryParseParenthesizedAtomic(t, symbols);
  if (par) return par;
  const simple = parseSimpleBool(t);
  if (simple) return simple;
  const id = parseIdentifierCondition(t, symbols);
  if (id) return id;
  const cmp = parseComparisonCondition(t, symbols);
  if (cmp) return cmp;
  return null;
}

function tryParseParenthesizedAtomic(t: string, symbols?: { [k: string]: { type: string; mutable: boolean } }): { valid: boolean; text?: string; usesStdint: boolean; usesStdbool: boolean } | null {
  // parentheses: if entire expression is parenthesized, strip and parse inner
  if (!(t[0] === '(' && t[t.length - 1] === ')')) return null;
  // ensure matching pair for outer parentheses and preserve them in output
  const match = findMatching(t, 0, '(', ')');
  if (match !== t.length - 1) return null;
  const inner = parseCondition(t.substring(1, t.length - 1).trim(), symbols);
  if (!inner.valid) return null;
  // Preserve parentheses only when inner contains logical operators which affect precedence
  if (inner.text && (inner.text.indexOf('||') !== -1 || inner.text.indexOf('&&') !== -1)) {
    return { valid: true, text: `(${inner.text})`, usesStdint: inner.usesStdint, usesStdbool: inner.usesStdbool };
  }
  return { valid: true, text: inner.text, usesStdint: inner.usesStdint, usesStdbool: inner.usesStdbool };
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

