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
  "*CStr": "uint8_t*",
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
  if (typeName === '*CStr') return { cType: 'const uint8_t*', usesStdint: true, usesStdbool: false, kind: 'ptr' };
  // support generic pointer types of the form *T
  if (typeName.length > 0 && typeName[0] === '*') {
    const inner = typeName.substring(1).trim();
    const innerInfo = getTypeInfo(inner);
    const base = innerInfo.cType || (typeMap[inner] || inner);
    const cType = base + '*';
    return { cType, usesStdint: innerInfo.usesStdint, usesStdbool: innerInfo.usesStdbool, kind: 'ptr' };
  }
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

// eslint-disable-next-line complexity
export function compile(input: string) {
  // remove comments (but preserve string/char literals) before trimming
  const src = stripCommentsRespectingStrings(input).trim();
  if (src === "") return "";
  try {

    // extract any leading import statements; keep their emitted includes and the remaining source
    const { importText, restSrc } = extractLeadingImports(src);
    if (importText && !restSrc) return importText;
    const remaining = restSrc || src;

    const fnPrefix = "fn ";
    // strip leading extern declarations (they emit nothing) and collect their signatures
    const { onlyExterns, restAfterExterns, externs } = extractLeadingExterns(remaining);
    if (onlyExterns) return importText || '';
    const afterExterns = restAfterExterns || remaining;
    if (afterExterns.startsWith(fnPrefix)) {
      // support multiple top-level functions in one input by parsing them sequentially
      const results: DeclResult[] = [];
      let s = afterExterns.trim();
      while (s.length > 0 && s.startsWith(fnPrefix)) {
        // find the => and the following body braces
        const arrowIndex = s.indexOf('=>');
        if (arrowIndex === -1) throw new Error('Invalid function declaration: ' + input);
        const braceIndex = s.indexOf('{', arrowIndex);
        if (braceIndex === -1) throw new Error('Invalid function body');
        const closeBrace = findMatching(s, braceIndex, '{', '}');
        if (closeBrace === -1) throw new Error('Unterminated function body');
        const funcSrc = s.substring(0, closeBrace + 1).trim();
        const decl = compileFunction(funcSrc, externs);
        results.push(decl);
        s = s.substring(closeBrace + 1).trim();
      }
      // combine results and emit includes once
      const needStdint = results.some(r => r.usesStdint);
      const needStdbool = results.some(r => r.usesStdbool);
      const combined = results.map(r => r.text).join(' ');
      const out = (importText || '') + emitWithIncludes({ text: combined, usesStdint: needStdint, usesStdbool: needStdbool });
      return out;
    }

    // support top-level if statements like: if(true){}
    // If the input is a single top-level if (no semicolons), handle it directly.
    if (isIfStatement(afterExterns) && afterExterns.indexOf(';') === -1) {
      const res = compileIfStatement(afterExterns);
      const out = emitWithIncludes(res);
      return importText ? importText + out : out;
    }

    const letPrefix = "let ";
    // support multi-statement inputs (e.g. 'let x = 0; if(x){}') by routing to compileStatements
    if (afterExterns.indexOf(';') === -1 && !afterExterns.startsWith(letPrefix)) {
      throw new Error("compile only supports empty input or simple let declarations");
    }
    const results = compileStatements(afterExterns);
    // If any declaration needs stdint, emit include once at top.
    const needStdint = results.some(r => r.usesStdint);
    const needStdbool = results.some(r => r.usesStdbool);
    const decls = results.map(r => r.text).join(" ");
    const final = emitWithIncludes({ text: decls, usesStdint: needStdint, usesStdbool: needStdbool });
    return importText ? importText + final : final;
  } catch (e) {
    const msg = e instanceof Error ? e.message : String(e);
    // Provide the original input and a short preview to help trace failures
    const preview = src.length > 200 ? src.substring(0, 200) + '...' : src;
    throw new Error(`Compilation error: ${msg}\nSource (trimmed): ${preview}`);
  }
}

// eslint-disable-next-line complexity
function extractLeadingImports(src: string): { importText: string; restSrc: string | null } {
  if (!src.startsWith('import ')) return { importText: '', restSrc: null };
  const parts = splitTopLevelBySemicolon(src);
  const includes: string[] = [];
  const seen = new Set<string>();
  let idx = 0;
  for (; idx < parts.length; idx++) {
    const p = parts[idx].trim();
    if (p.length === 0) continue;
    if (!p.startsWith('import ')) break;
    let rest = p.substring('import '.length).trim();
    if (rest.endsWith(';')) rest = rest.substring(0, rest.length - 1).trim();
    if (!isValidIdentifier(rest)) throw new Error('Invalid import name: ' + rest);
    if (seen.has(rest)) throw new Error('Duplicate import: ' + rest);
    seen.add(rest);
    includes.push(`#include <${rest}.h>`);
  }
  if (includes.length === 0) return { importText: '', restSrc: null };
  const importText = includes.map(i => i + '\r\n').join('');
  // rebuild rest source from remaining parts (keeping original separators)
  const remainingParts = parts.slice(idx).filter(p => p.trim().length > 0);
  if (remainingParts.length === 0) return { importText, restSrc: null };
  // join with semicolons and append a trailing semicolon when appropriate
  const restSrc = (remainingParts.join(';') + (src.trim().endsWith(';') ? ';' : '')).trim();
  return { importText, restSrc };
}

function emitWithIncludes(res: { text: string; usesStdint: boolean; usesStdbool: boolean }) {
  const includes: string[] = [];
  if (res.usesStdbool) includes.push('#include <stdbool.h>');
  if (res.usesStdint) includes.push('#include <stdint.h>');
  if (includes.length === 0) return res.text;
  return includes.join('\n') + '\n' + res.text;
}

// tryHandleExtern removed; extractLeadingExterns handles extern declarations now.

function extractLeadingExterns(src: string): { onlyExterns: boolean; restAfterExterns: string | null; externs: { [k: string]: { params: Param[]; returnType: string } } } {
  const parts = splitTopLevelBySemicolon(src);
  let idx = 0;
  const externs: { [k: string]: { params: Param[]; returnType: string } } = {};
  for (; idx < parts.length; idx++) {
    const p = parts[idx].trim();
    if (p.length === 0) continue;
    if (!p.startsWith('extern fn ')) break;
    const afterExtern = p.substring('extern '.length).trim();
    const parsedHeader = parseFunctionHeader(afterExtern);
    const rest = parsedHeader.afterParams.trim();
    if (!rest.startsWith(':')) throw new Error('Missing return type for extern function');
    const { returnType } = extractReturnAndBody(rest + '=> {}');
    externs[parsedHeader.name] = { params: parsedHeader.params || [], returnType };
    // valid extern; continue
  }
  const remainingParts = parts.slice(idx).filter(p => p.trim().length > 0);
  if (remainingParts.length === 0) return { onlyExterns: true, restAfterExterns: null, externs };
  const rest = remainingParts.join(';') + (src.trim().endsWith(';') ? ';' : '');
  return { onlyExterns: false, restAfterExterns: rest.trim(), externs };
}

// removed: tryHandleImport is superseded by extractLeadingImports

function compileStatements(src: string): DeclResult[] {
  try {
    const letPrefix = "let ";
    const parts = splitTopLevelBySemicolon(src);
    const results: DeclResult[] = [];
    // symbol table tracks declared variables: name -> {type, mutable}
    const symbols: { [k: string]: { type: string; mutable: boolean } } = {};

    for (const p of parts) {
      const s = p.trim();
      if (s.length === 0) continue;
      // Ensure identifiers used in this statement are declared when required
      ensureIdentifiersDeclared(s, symbols);
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
  } catch (e) {
    const msg = e instanceof Error ? e.message : String(e);
    throw new Error(`compileStatements error: ${msg}\nSource: ${src}\nSymbols snapshot: ${JSON.stringify({})}`);
  }
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
  if (symbols[name]) throw new Error('Duplicate variable declaration: ' + name);
  const decl = typeName ? compileTypedDeclaration(name, typeName, value, symbols) : compileUntypedDeclaration(name, value, symbols);
  symbols[name] = { type: decl.declaredType || (typeName || 'I32'), mutable: !!isMutable };
  return decl;
}

function handleAddressOfAssignment(value: string, varType: string, symbols: { [k: string]: { type: string; mutable: boolean } } | undefined, varName: string): DeclResult | null {
  const addrMatch = value.trim().match(/^&([A-Za-z_][A-Za-z0-9_]*)$/);
  if (!addrMatch) return null;
  const target = addrMatch[1];
  if (!symbols || !symbols[target]) throw new Error('Address-of target not declared: ' + target);
  if (!(varType.length > 0 && varType[0] === '*')) throw new Error('Assignment type mismatch: expected pointer type');
  const inner = varType.substring(1).trim();
  if (symbols[target].type !== inner) throw new Error('Assignment type mismatch: pointer inner type does not match target');
  return { text: `${varName} = &${target};`, usesStdint: getTypeInfo(inner).usesStdint, usesStdbool: false };
}

function handleDerefAssignment(value: string, varType: string, symbols: { [k: string]: { type: string; mutable: boolean } } | undefined, varName: string): DeclResult | null {
  const derefMatch = value.trim().match(/^\*([A-Za-z_][A-Za-z0-9_]*)$/);
  if (!derefMatch) return null;
  const ptr = derefMatch[1];
  if (!symbols || !symbols[ptr]) throw new Error('Dereference of undeclared identifier: ' + ptr);
  const ptype = symbols[ptr].type;
  if (!ptype || ptype[0] !== '*') throw new Error('Dereference of non-pointer: ' + ptr);
  const inner = ptype.substring(1).trim();
  if (varType[0] === '*') throw new Error('Assignment type mismatch: cannot assign dereferenced pointer to pointer variable');
  if (varType !== inner) throw new Error('Assignment type mismatch: expected ' + varType + ' but got dereferenced ' + inner);
  const info = getTypeInfo(inner);
  return { text: `${varName} = *${ptr};`, usesStdint: info.usesStdint, usesStdbool: info.usesStdbool };
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
  const addrHandled = handleAddressOfAssignment(value, varType, symbols, varName);
  if (addrHandled) return addrHandled;
  const derefHandled = handleDerefAssignment(value, varType, symbols, varName);
  if (derefHandled) return derefHandled;
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
  // handle array, CStr/pointer types, and dereference RHS in a helper to reduce complexity
  const special = compileTypedDeclarationSpecial(name, typeName, value, symbols);
  if (special) return special;
  if (supportedTypes.indexOf(typeName) === -1) throw new Error("Unsupported type");
  const info = getTypeInfo(typeName);
  if (info.kind === 'int' || info.kind === 'uint') return compileIntegerTyped(name, typeName, value);
  if (info.kind === 'float') return compileFloatTyped(name, typeName, value);
  if (info.kind === 'bool') return compileBooleanTyped(name, typeName, value, symbols);
  throw new Error("Unsupported type category");
}

function compileTypedDeclarationSpecial(name: string, typeName: string, value: string, symbols?: { [k: string]: { type: string; mutable: boolean } }): DeclResult | null {
  // support array types of form: [T; N]
  if (typeName.startsWith('[')) return compileArrayTyped(name, typeName, value);
  // pointer types like *CStr
  if (typeName === '*CStr') return compilePointerTyped(name, typeName, value);
  if (typeName.length > 0 && typeName[0] === '*') return compilePointerTypedGeneric(name, typeName, value, symbols);
  const deref = compileTypedDereference(name, typeName, value, symbols);
  if (deref) return deref;
  return null;
}

function compileTypedDereference(name: string, typeName: string, value: string, symbols?: { [k: string]: { type: string; mutable: boolean } }): DeclResult | null {
  const derefMatch = value.trim().match(/^\*([A-Za-z_][A-Za-z0-9_]*)$/);
  if (!derefMatch) return null;
  const ptr = derefMatch[1];
  if (!symbols || !symbols[ptr]) throw new Error('Dereference of undeclared identifier: ' + ptr);
  const ptype = symbols[ptr].type;
  if (!ptype || ptype[0] !== '*') throw new Error('Dereference of non-pointer: ' + ptr);
  const inner = ptype.substring(1).trim();
  if (inner !== typeName) throw new Error('Type mismatch: expected ' + typeName + ' from dereferenced ' + ptr);
  const info = getTypeInfo(typeName);
  const outType = info.cType || (typeMap[typeName] || typeName);
  return { text: `${outType} ${name} = *${ptr};`, usesStdint: info.usesStdint, usesStdbool: info.usesStdbool, declaredType: typeName };
}

function compilePointerTypedGeneric(name: string, typeName: string, value: string, symbols?: { [k: string]: { type: string; mutable: boolean } }): DeclResult {
  const inner = typeName.substring(1).trim();
  // value must be '&ident' where ident exists and matches inner type
  const v = value.trim();
  if (!v.startsWith('&')) throw new Error('Type mismatch: expected address-of expression');
  const target = v.substring(1).trim();
  if (!isValidIdentifier(target)) throw new Error('Invalid identifier for address-of');
  if (!symbols || !symbols[target]) throw new Error('Address-of target not declared: ' + target);
  const targetType = symbols[target].type;
  if (targetType !== inner) throw new Error('Address-of target type mismatch');
  const info = getTypeInfo(typeName);
  return { text: `${info.cType} ${name} = &${target};`, usesStdint: info.usesStdint, usesStdbool: info.usesStdbool, declaredType: typeName };
}

function compilePointerTyped(name: string, typeName: string, value: string): DeclResult {
  const v = value.trim();
  // only allow string literals for *CStr for now
  if (!(v.length >= 2 && v[0] === '"' && v[v.length - 1] === '"')) throw new Error('Type mismatch: expected string literal');
  // emit const char* for C strings
  const cType = 'const ' + (typeMap[typeName] || typeName);
  // ensure *CStr maps to const uint8_t*
  return { text: `${cType} ${name} = ${v};`, usesStdint: true, usesStdbool: false, declaredType: typeName };
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
  // Accept char literal for U8 typed declarations: let x : U8 = 'a';
  if (isCharLiteral(value)) {
    if (typeName !== 'U8') throw new Error('Type mismatch: char literal only allowed for U8');
    const code = value.charCodeAt(1);
    const cType = typeMap[typeName] || typeName;
    return { text: `${cType} ${name} = ${code};`, usesStdint: true, usesStdbool: false, declaredType: typeName };
  }
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

function untypedAddressOf(name: string, value: string, symbols?: { [k: string]: { type: string; mutable: boolean } }): DeclResult | null {
  const addrMatch = value.trim().match(/^&([A-Za-z_][A-Za-z0-9_]*)$/);
  if (!addrMatch) return null;
  const target = addrMatch[1];
  if (!symbols || !symbols[target]) throw new Error('Address-of target not declared: ' + target);
  const ttype = symbols[target].type;
  const cType = (getTypeInfo('*' + ttype).cType) || (typeMap[ttype] || ttype) + '*';
  return { text: `${cType} ${name} = &${target};`, usesStdint: getTypeInfo(ttype).usesStdint, usesStdbool: false, declaredType: '*' + ttype };
}

function untypedDereference(name: string, value: string, symbols?: { [k: string]: { type: string; mutable: boolean } }): DeclResult | null {
  const derefMatch = value.trim().match(/^\*([A-Za-z_][A-Za-z0-9_]*)$/);
  if (!derefMatch) return null;
  const ptr = derefMatch[1];
  if (!symbols || !symbols[ptr]) throw new Error('Dereference of undeclared identifier: ' + ptr);
  const ptype = symbols[ptr].type;
  if (!ptype || ptype[0] !== '*') throw new Error('Dereference of non-pointer: ' + ptr);
  const inner = ptype.substring(1).trim();
  const info = getTypeInfo(inner);
  const outType = info.cType || (typeMap[inner] || inner);
  return { text: `${outType} ${name} = *${ptr};`, usesStdint: info.usesStdint, usesStdbool: info.usesStdbool, declaredType: inner };
}

function compileUntypedDeclaration(name: string, value: string, symbols?: { [k: string]: { type: string; mutable: boolean } }): DeclResult {
  type Handler = (n: string, v: string, s?: { [k: string]: { type: string; mutable: boolean } }) => DeclResult | null;
  const handlers: Handler[] = [
    tryHandleIndexUntyped,
    untypedAddressOf,
    untypedDereference,
    tryHandleCharUntyped,
    tryHandleArrayUntyped,
  ];
  for (const h of handlers) {
    const res = h(name, value, symbols);
    if (res) return res;
  }
  return finalizeUntypedDeclaration(name, value, symbols);
}

function finalizeUntypedDeclaration(name: string, value: string, symbols?: { [k: string]: { type: string; mutable: boolean } }): DeclResult {
  const condParsed = parseCondition(value, symbols);
  if (condParsed.valid) {
    return { text: `bool ${name} = ${condParsed.text};`, usesStdint: condParsed.usesStdint, usesStdbool: true, declaredType: 'Bool' };
  }
  let inferred = "I32";
  if (isCharLiteral(value)) {
    inferred = 'U8';
    const outType = typeMap[inferred] || 'uint8_t';
    return { text: `${outType} ${name} = ${value.charCodeAt(1)};`, usesStdint: true, usesStdbool: false, declaredType: inferred };
  }
  const floatInf = inferFloatSuffix(value);
  inferred = floatInf.inferred;
  value = floatInf.value;
  const outType = typeMap[inferred] || typeMap["I32"] || "int32_t";
  if (inferred[0] === 'I' || inferred[0] === 'U') {
    return { text: `${outType} ${name} = ${value};`, usesStdint: true, usesStdbool: false, declaredType: inferred };
  }
  return { text: `${outType} ${name} = ${value};`, usesStdint: false, usesStdbool: false, declaredType: inferred };
}

// eslint-disable-next-line complexity
function tryHandleIndexUntyped(name: string, value: string, symbols?: { [k: string]: { type: string; mutable: boolean } }): DeclResult | null {
  const idxMatch = value.trim().match(/^([A-Za-z_][A-Za-z0-9_]*)\[(\d+)\]$/);
  if (!idxMatch) return null;
  const arrName = idxMatch[1];
  const idx = idxMatch[2];
  if (!symbols || !symbols[arrName]) throw new Error('Indexing into undeclared identifier: ' + arrName);
  const symType = symbols[arrName].type;
  // pointer to C string -> return C char
  if (symType === '*CStr') {
    // If the symbol table is the parameter symbol table (inside a function),
    // treat indexing as yielding an unsigned byte (uint8_t). For top-level
    // variables, keep returning a plain C char to match previous behavior.
    if (symbols && (symbols as unknown as { __isParamSymbols?: boolean }).__isParamSymbols) {
      return { text: `uint8_t ${name} = ${arrName}[${idx}];`, usesStdint: true, usesStdbool: false, declaredType: 'U8' };
    }
    return { text: `char ${name} = ${arrName}[${idx}];`, usesStdint: false, usesStdbool: false, declaredType: 'char' };
  }
  // array types like [T; N]
  if (symType && symType.startsWith('[')) {
    const parsed = parseArrayType(symType);
    const elemType = parsed.elemType;
    const info = getTypeInfo(elemType);
    const cType = typeMap[elemType] || elemType;
    // if element is a U8 (e.g. char), ensure we return uint8_t and mark stdint usage
    const declared = elemType === 'U8' ? 'U8' : elemType;
    return { text: `${cType} ${name} = ${arrName}[${idx}];`, usesStdint: info.usesStdint, usesStdbool: info.usesStdbool, declaredType: declared };
  }
  // if symbol exists but is not pointer/array, indexing is invalid
  throw new Error('Indexing into non-array/ptr value: ' + arrName);
}

function copyStringLiteral(src: string, start: number): { text: string; nextIndex: number } {
  let out = '"';
  let j = start + 1;
  const n = src.length;
  while (j < n) {
    const cj = src[j];
    out += cj;
    if (cj === '"') return { text: out, nextIndex: j + 1 };
    if (cj === '\\') { j++; if (j < n) { out += src[j]; j++; } continue; }
    j++;
  }
  return { text: out, nextIndex: j };
}

function copyCharLiteral(src: string, start: number): { text: string; nextIndex: number } {
  let out = "'";
  let j = start + 1;
  const n = src.length;
  while (j < n) {
    const cj = src[j];
    out += cj;
    if (cj === "'") return { text: out, nextIndex: j + 1 };
    if (cj === '\\') { j++; if (j < n) { out += src[j]; j++; } continue; }
    j++;
  }
  return { text: out, nextIndex: j };
}

function stripCommentsRespectingStrings(src: string): string {
  let out = '';
  let i = 0;
  const n = src.length;
  while (i < n) {
    const ch = src[i];
    if (ch === '"') {
      const res = copyStringLiteral(src, i);
      out += res.text;
      i = res.nextIndex;
      continue;
    }
    if (ch === "'") {
      const res = copyCharLiteral(src, i);
      out += res.text;
      i = res.nextIndex;
      continue;
    }
    if (ch === '/' && i + 1 < n && src[i + 1] === '/') {
      i = skipSingleLineComment(src, i);
      continue;
    }
    if (ch === '/' && i + 1 < n && src[i + 1] === '*') {
      i = skipBlockComment(src, i);
      continue;
    }
    out += ch;
    i++;
  }
  return out;
}

function skipSingleLineComment(src: string, start: number): number {
  const n = src.length;
  let i = start + 2;
  while (i < n && src[i] !== '\n' && src[i] !== '\r') i++;
  return i;
}

function skipBlockComment(src: string, start: number): number {
  const n = src.length;
  let i = start + 2;
  while (i + 1 < n && !(src[i] === '*' && src[i + 1] === '/')) i++;
  return Math.min(n, i + 2);
}

// Ensure that identifiers appearing in a statement are declared in symbols when required.
function ensureIdentifiersDeclared(stmt: string, symbols: { [k: string]: { type: string; mutable: boolean } }) {
  // naive scan: find identifiers via regex, ignore numeric literals, string/char literals
  // remove string and char literals first
  let s = stmt.replace(/"(?:\\.|[^"\\])*"/g, '');
  s = s.replace(/'(?:\\.|[^'\\])*'/g, '');
  // find tokens that look like identifiers
  const idRegex = /\b([A-Za-z_][A-Za-z0-9_]*)\b/g;
  let m: RegExpExecArray | null;
  const keywords = new Set(['let', 'mut', 'if', 'fn', 'extern', 'import', 'return', 'true', 'false']);
  const allowedBuiltins = new Set(['strlen']);
  // allow language type names and CStr marker
  const allowedTypeNames = new Set(supportedTypes.concat(['CStr']));
  while ((m = idRegex.exec(s)) !== null) {
    const id = m[1];
    if (keywords.has(id)) continue;
    if (allowedBuiltins.has(id)) continue;
    // if appears as a left-side in a declaration (let id = ...) skip check here since declaration will add symbol
    // but our caller calls ensureIdentifiersDeclared before processing declaration, so detect pattern 'let id ='
    const declPattern = new RegExp('^\\s*let\\s+(?:mut\\s+)?' + id + '\\b');
    if (declPattern.test(s)) continue;
    // ignore type names like I32, U8, Bool, F32, etc and CStr
    if (allowedTypeNames.has(id)) continue;
    // if identifier is declared in symbols, ok
    if (symbols && symbols[id]) continue;
    // if used as function name in a call like foo(...) and foo is not builtin, allow for now only if declared as extern earlier
    // check for pattern id(  -> treat as function call; unless it's strlen, consider unknown
    const callPattern = new RegExp('\\b' + id + '\\s*\\(');
    if (callPattern.test(s) && allowedBuiltins.has(id)) continue;
    // otherwise it's an undeclared identifier
    throw new Error('Undeclared identifier: ' + id);
  }
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

// eslint-disable-next-line complexity
function compileFunction(src: string, externs?: { [k: string]: { params: Param[]; returnType: string } }): DeclResult {
  try {
    // Expect form: fn name() : Type => { }
    const { name, params, afterParams } = parseFunctionHeader(src);
    const { returnType, body } = extractReturnAndBody(afterParams);
    // if there was an extern with this name, ensure signature matches
    if (externs && externs[name]) {
      const e = externs[name];
      // compare return types
      if (e.returnType !== returnType) throw new Error('Extern declaration mismatch for ' + name);
      // compare number of params and types/names
      if ((e.params && e.params.length) !== (params && params.length)) throw new Error('Extern declaration mismatch for ' + name);
      for (let i = 0; i < (params || []).length; i++) {
        const p = params![i];
        const ep = e.params[i];
        if (p.type !== ep.type) throw new Error('Extern declaration mismatch for ' + name);
      }
    }
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
  } catch (e) {
    const msg = e instanceof Error ? e.message : String(e);
    throw new Error(`compileFunction error: ${msg}\nFunction source: ${src}`);
  }
}

function buildParamSymbols(params: Param[]): { [k: string]: { type: string; mutable: boolean } } {
  const symbols: { [k: string]: { type: string; mutable: boolean } } = {};
  if (!params || params.length === 0) return symbols;
  for (const p of params) symbols[p.name] = { type: p.type, mutable: false };
  // mark this symbol table as coming from function parameters so callers
  // can special-case parameter semantics when needed.
  (symbols as unknown as { __isParamSymbols?: boolean }).__isParamSymbols = true;
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
  // Support multiple parameters separated by commas: name : Type, other : Type
  const parts = inner.split(',').map(p => p.trim()).filter(p => p.length > 0);
  const params: Param[] = [];
  const seen = new Set<string>();
  for (const part of parts) {
    const colon = part.indexOf(':');
    if (colon === -1) throw new Error('Invalid parameter declaration');
    const pname = part.substring(0, colon).trim();
    const ptype = part.substring(colon + 1).trim();
    if (!isValidIdentifier(pname)) throw new Error('Invalid parameter name');
    if (seen.has(pname)) throw new Error('Duplicate parameter name: ' + pname);
    seen.add(pname);
    params.push({ name: pname, type: ptype });
  }
  return { params, restAfterParams: restAfter };
}

function buildParamInfo(params: Param[]): { paramText: string; usesStdint: boolean } {
  if (!params || params.length === 0) return { paramText: '', usesStdint: false };
  const parts: string[] = [];
  let usesStdint = false;
  for (const p of params) {
    const info = getTypeInfo(p.type);
    if (info.kind === 'other') throw new Error('Unsupported parameter type');
    parts.push(`${info.cType} ${p.name}`);
    usesStdint = usesStdint || info.usesStdint;
  }
  return { paramText: parts.join(', '), usesStdint };
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
  if (!parsed.valid) throw new Error('Unsupported if condition: ' + cond);
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

// eslint-disable-next-line complexity
function sideInfo(side: string, symbols?: { [k: string]: { type: string; mutable: boolean } }) {
  if (isValidIdentifier(side)) {
    if (!symbols || !symbols[side]) return { kind: 'unknown' };
    const t = symbols[side].type;
    const info = getTypeInfo(t);
    return { kind: 'ident', text: side, cKind: info.kind, usesStdint: info.usesStdint };
  }
  // support simple function calls like strlen("...") returning known types
  const fnMatch = side.match(/^([A-Za-z_][A-Za-z0-9_]*)\((.*)\)$/);
  if (fnMatch) {
    const fname = fnMatch[1];
    const farg = fnMatch[2].trim();
    // only allow string literal or identifier argument for now
    const isStringLiteral = farg.length >= 2 && farg[0] === '"' && farg[farg.length - 1] === '"';
    const isIdent = isValidIdentifier(farg);
    if (!isStringLiteral && !isIdent) return { kind: 'unknown' };
    if (fname === 'strlen') {
      // strlen returns a USize (size_t) -> treat as unsigned integer kind
      return { kind: 'call', text: `${fname}(${farg})`, cKind: 'uint', usesStdint: false };
    }
    return { kind: 'unknown' };
  }
  const nk = numericKind(side);
  if (nk.kind === 'unknown') return { kind: 'unknown' };
  const text = nk.suffix.length !== 0 ? side.substring(0, side.length - nk.suffix.length) : side;
  return { kind: 'literal', text, cKind: nk.kind };
}

// eslint-disable-next-line complexity
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

  // allow a common special-case: comparing an unsigned size (USize/uint)
  // with an integer literal zero (e.g. strlen(...) == 0). In general we
  // require matching numeric kinds, but accept uint vs int when the other
  // side is the literal 0.
  if (!leftKind || !rightKind) return null;
  if (leftKind !== rightKind) {
    // allow comparing unsigned values (like size_t/uint) with integer literals
    // as long as the literal is non-negative (e.g. 65). Do not allow negative
    // literals to be compared with unsigned types.
    const leftIsLiteral = L.kind === 'literal';
    const rightIsLiteral = R.kind === 'literal';
    const leftLiteralNonNeg = leftIsLiteral && !String(L.text).startsWith('-');
    const rightLiteralNonNeg = rightIsLiteral && !String(R.text).startsWith('-');
    const specialOk = (leftKind === 'uint' && rightLiteralNonNeg) || (rightKind === 'uint' && leftLiteralNonNeg);
    if (!specialOk) throw new Error('Type mismatch in comparison: operand kinds differ, left: ' + leftKind + ', right: ' + rightKind);
  }
  const usesStdint = !!(L.usesStdint || R.usesStdint);
  const leftText = L.text || leftRaw;
  const rightText = R.text || rightRaw;
  return { valid: true, text: `${leftText} ${op} ${rightText}`, usesStdint, usesStdbool: false };
}

// compareKindsCompatible removed — logic handled inline in parseComparisonCondition

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

// eslint-disable-next-line complexity
function parseAtomicBoolean(s: string, symbols?: { [k: string]: { type: string; mutable: boolean } }): { valid: boolean; text?: string; usesStdint: boolean; usesStdbool: boolean } | null {
  const t = s.trim();
  if (t.length === 0) return null;
  // handle unary not: !<atomic>
  if (t[0] === '!') {
    const inner = t.substring(1).trim();
    const atom = parseAtomicBoolean(inner, symbols);
    if (!atom || !atom.valid) return null;
    const needsParens = !!(atom.text && (atom.text.indexOf(' ') !== -1 || atom.text.indexOf('||') !== -1 || atom.text.indexOf('&&') !== -1));
    const text = atom.text ? (`!${needsParens ? `(${atom.text})` : atom.text}`) : '!';
    return { valid: true, text, usesStdint: atom.usesStdint, usesStdbool: true };
  }
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
  if (lit !== 'true' && lit !== 'false') throw new Error('Only simple boolean return statements supported: ' + lit);
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

// eslint-disable-next-line complexity
function compileFunctionBoolReturn(cReturn: string, name: string, returnType: string, body: string, paramText: string, paramUsesStdint: boolean, symbols?: { [k: string]: { type: string; mutable: boolean } }): DeclResult {
  const inner = stripBraces(body);
  if (inner.startsWith('return')) {
    const lit = extractReturnLiteral(inner);
    // allow identifiers, simple boolean literals, comparisons or full logical expressions
    if (isValidIdentifier(lit)) {
      const params = paramText ? paramText : '';
      return { text: `${cReturn} ${name}(${params}){return ${lit};}`, usesStdint: paramUsesStdint, usesStdbool: false, declaredType: returnType };
    }
    const simple = parseSimpleBool(lit);
    if (simple) {
      const params = paramText ? paramText : '';
      return { text: `${cReturn} ${name}(${params}){return ${simple.text};}`, usesStdint: simple.usesStdint || paramUsesStdint, usesStdbool: true, declaredType: returnType };
    }
    const cmp = tryParseComparison(lit);
    if (cmp) {
      const params = paramText ? paramText : '';
      return { text: `${cReturn} ${name}(${params}){return ${cmp.left} ${cmp.op} ${cmp.right};}`, usesStdint: paramUsesStdint || false, usesStdbool: true, declaredType: returnType };
    }
    const parsed = parseCondition(lit, symbols);
    if (parsed.valid) {
      const params = paramText ? paramText : '';
      return { text: `${cReturn} ${name}(${params}){return ${parsed.text};}`, usesStdint: paramUsesStdint || parsed.usesStdint, usesStdbool: parsed.usesStdbool, declaredType: returnType };
    }
    // fallback to strict literal validation
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
  if (last.startsWith('return')) return tryPreludeReturn(kind, cReturn, name, returnType, params, paramUsesStdint, beforeCompiled, last, symbols);
  if (last.startsWith('if')) return tryPreludeIf(kind, cReturn, name, returnType, params, paramUsesStdint, beforeCompiled, last, symbols);
  return null;
}

function makeReturnDecl(cReturn: string, name: string, params: string, bodyText: string, usesStdint: boolean, usesStdbool: boolean, declaredType: string): DeclResult {
  return { text: `${cReturn} ${name}(${params}){${bodyText}}`, usesStdint, usesStdbool, declaredType };
}

function preludeReturnIdent(kind: 'int' | 'float' | 'bool', cReturn: string, name: string, params: string, paramUsesStdint: boolean, beforeCompiled: { text: string; usesStdint: boolean; usesStdbool: boolean }, lit: string, returnType: string): DeclResult {
  const bodyText = beforeCompiled.text + `return ${lit};`;
  const usesStdint = paramUsesStdint || beforeCompiled.usesStdint || (kind === 'int');
  const usesStdbool = beforeCompiled.usesStdbool;
  return makeReturnDecl(cReturn, name, params, bodyText, usesStdint, usesStdbool, returnType);
}

function preludeReturnInt(cReturn: string, name: string, params: string, paramUsesStdint: boolean, beforeCompiled: { text: string; usesStdint: boolean; usesStdbool: boolean }, lit: string, returnType: string): DeclResult {
  validateIntegerLiteral(lit);
  const bodyText = beforeCompiled.text + `return ${lit};`;
  return makeReturnDecl(cReturn, name, params, bodyText, true || paramUsesStdint || beforeCompiled.usesStdint, beforeCompiled.usesStdbool, returnType);
}

function preludeReturnFloat(cReturn: string, name: string, params: string, paramUsesStdint: boolean, beforeCompiled: { text: string; usesStdint: boolean; usesStdbool: boolean }, lit: string, returnType: string): DeclResult {
  validateFloatLiteral(lit);
  const bodyText = beforeCompiled.text + `return ${lit};`;
  return makeReturnDecl(cReturn, name, params, bodyText, paramUsesStdint || beforeCompiled.usesStdint, beforeCompiled.usesStdbool, returnType);
}

function preludeReturnBool_Simple(cReturn: string, name: string, params: string, paramUsesStdint: boolean, beforeCompiled: { text: string; usesStdint: boolean; usesStdbool: boolean }, lit: string, returnType: string): DeclResult | null {
  const simple = parseSimpleBool(lit);
  if (!simple) return null;
  const bodyText = beforeCompiled.text + `return ${simple.text};`;
  return makeReturnDecl(cReturn, name, params, bodyText, paramUsesStdint || beforeCompiled.usesStdint || simple.usesStdint, true || beforeCompiled.usesStdbool, returnType);
}

function preludeReturnBool_Comp(cReturn: string, name: string, params: string, paramUsesStdint: boolean, beforeCompiled: { text: string; usesStdint: boolean; usesStdbool: boolean }, lit: string, returnType: string): DeclResult | null {
  const cmp = tryParseComparison(lit);
  if (!cmp) return null;
  const bodyText = beforeCompiled.text + `return ${cmp.left} ${cmp.op} ${cmp.right};`;
  return makeReturnDecl(cReturn, name, params, bodyText, paramUsesStdint || beforeCompiled.usesStdint, true || beforeCompiled.usesStdbool, returnType);
}

function preludeReturnBool_Parsed(cReturn: string, name: string, params: string, paramUsesStdint: boolean, beforeCompiled: { text: string; usesStdint: boolean; usesStdbool: boolean }, lit: string, returnType: string, symbols?: { [k: string]: { type: string; mutable: boolean } }): DeclResult | null {
  const parsed = parseCondition(lit, symbols);
  if (!parsed.valid) return null;
  const bodyText = beforeCompiled.text + `return ${parsed.text};`;
  return makeReturnDecl(cReturn, name, params, bodyText, paramUsesStdint || beforeCompiled.usesStdint || parsed.usesStdint, true || beforeCompiled.usesStdbool || parsed.usesStdbool, returnType);
}

function preludeReturnBool(cReturn: string, name: string, params: string, paramUsesStdint: boolean, beforeCompiled: { text: string; usesStdint: boolean; usesStdbool: boolean }, lit: string, returnType: string, symbols?: { [k: string]: { type: string; mutable: boolean } }): DeclResult | null {
  return preludeReturnBool_Simple(cReturn, name, params, paramUsesStdint, beforeCompiled, lit, returnType)
    || preludeReturnBool_Comp(cReturn, name, params, paramUsesStdint, beforeCompiled, lit, returnType)
    || preludeReturnBool_Parsed(cReturn, name, params, paramUsesStdint, beforeCompiled, lit, returnType, symbols);
}

function tryPreludeReturn(kind: 'int' | 'float' | 'bool', cReturn: string, name: string, returnType: string, params: string, paramUsesStdint: boolean, beforeCompiled: { text: string; usesStdint: boolean; usesStdbool: boolean }, last: string, symbols?: { [k: string]: { type: string; mutable: boolean } }): DeclResult | null {
  const lit = extractReturnLiteral(last);
  // identifier allowed for all kinds
  if (isValidIdentifier(lit)) return preludeReturnIdent(kind, cReturn, name, params, paramUsesStdint, beforeCompiled, lit, returnType);
  if (kind === 'int') return preludeReturnInt(cReturn, name, params, paramUsesStdint, beforeCompiled, lit, returnType);
  if (kind === 'float') return preludeReturnFloat(cReturn, name, params, paramUsesStdint, beforeCompiled, lit, returnType);
  // kind === 'bool'
  const boolRes = preludeReturnBool(cReturn, name, params, paramUsesStdint, beforeCompiled, lit, returnType, symbols);
  if (boolRes) return boolRes;
  // fallback: strict boolean literal validation
  validateBoolLiteral(lit);
  const bodyText = beforeCompiled.text + `return ${lit};`;
  return makeReturnDecl(cReturn, name, params, bodyText, paramUsesStdint || beforeCompiled.usesStdint, true || beforeCompiled.usesStdbool, returnType);
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

