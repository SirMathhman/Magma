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
  F32: "float",
  F64: "double",
};

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

  const fnPrefix = "fn ";
  if (src.startsWith(fnPrefix)) {
    return compileFunction(src);
  }

  const letPrefix = "let ";
  if (!src.startsWith(letPrefix) || !src.endsWith(";")) {
    throw new Error("compile only supports empty input or simple let declarations");
  }
  const results = compileStatements(src);
  // If any declaration needs stdint, emit include once at top.
  const needStdint = results.some(r => r.usesStdint);
  const needStdbool = results.some(r => r.usesStdbool);
  const decls = results.map(r => r.text).join(" ");
  const includes: string[] = [];
  if (needStdbool) includes.push('#include <stdbool.h>');
  if (needStdint) includes.push('#include <stdint.h>');
  if (includes.length === 0) return decls;
  return includes.join('\n') + '\n' + decls;
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
  if (!(value === 'true' || value === 'false')) throw new Error('Type mismatch: expected boolean literal');
  return { text: `${name} = ${value};`, usesStdint: false, usesStdbool: false };
}

function compileTypedDeclaration(name: string, typeName: string, value: string): DeclResult {
  if (supportedTypes.indexOf(typeName) === -1) throw new Error("Unsupported type");
  if (typeName[0] === 'I' || typeName[0] === 'U') return compileIntegerTyped(name, typeName, value);
  if (typeName === "F32" || typeName === "F64") return compileFloatTyped(name, typeName, value);
  if (typeName === 'Bool') return compileBooleanTyped(name, typeName, value);
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
  if (!(val === 'true' || val === 'false')) throw new Error('Type mismatch: expected boolean literal');
  const cType = typeMap[typeName] || 'bool';
  return { text: `${cType} ${name} = ${val};`, usesStdint: false, usesStdbool: true, declaredType: typeName };
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

function compileFunction(src: string): string {
  // Expect form: fn name() : Type => { }
  if (!src.startsWith('fn ')) throw new Error('Invalid function declaration');
  const rest = src.substring(3).trim();
  const { name, restAfterName } = extractFunctionName(rest);
  if (!isValidIdentifier(name)) throw new Error('Invalid identifier');
  const afterParams = validateEmptyParams(restAfterName);
  const { returnType, body } = extractReturnAndBody(afterParams);
  if (supportedTypes.indexOf(returnType) === -1) throw new Error('Unsupported return type');
  validateEmptyBody(body);
  const cReturn = typeMap[returnType] || returnType;
  return `${cReturn} ${name}(){}`;
}

function extractFunctionName(src: string): { name: string; restAfterName: string } {
  const parenIndex = src.indexOf('(');
  if (parenIndex === -1) throw new Error('Invalid function declaration');
  const name = src.substring(0, parenIndex).trim();
  const restAfterName = src.substring(parenIndex);
  return { name, restAfterName };
}

function validateEmptyParams(src: string): string {
  if (!src.startsWith('()')) throw new Error('Only empty parameter lists supported');
  return src.substring(2).trim();
}

function extractReturnAndBody(src: string): { returnType: string; body: string } {
  if (!src.startsWith(':')) throw new Error('Missing return type');
  const rest = src.substring(1).trim();
  const arrowIndex = rest.indexOf('=>');
  if (arrowIndex === -1) throw new Error('Invalid function declaration');
  const returnType = rest.substring(0, arrowIndex).trim();
  const body = rest.substring(arrowIndex + 2).trim();
  return { returnType, body };
}

function validateEmptyBody(body: string) {
  if (body !== '{}' && body !== '{ }') throw new Error('Only empty function bodies supported');
}
