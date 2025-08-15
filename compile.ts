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
type DeclResult = { text: string; usesStdint: boolean };

export function compile(input: string) {
  const src = input.trim();
  if (src === "") return "";

  const letPrefix = "let ";
  if (!src.startsWith(letPrefix) || !src.endsWith(";")) {
    throw new Error("compile only supports empty input or simple let declarations");
  }
  const results = compileStatements(src);
  // If any declaration needs stdint, emit include once at top.
  const needStdint = results.some(r => r.usesStdint);
  const decls = results.map(r => r.text).join(" ");
  return needStdint ? `#include <stdint.h>\n${decls}` : decls;
}

function compileStatements(src: string): DeclResult[] {
  const letPrefix = "let ";
  const parts = src.split(";");
  const results: DeclResult[] = [];
  for (const p of parts) {
    const s = p.trim();
    if (s.length === 0) continue;
    const stmt = s + ";"; // re-add semicolon for parsing convenience
    if (!stmt.startsWith(letPrefix) || !stmt.endsWith(";")) throw new Error("compile only supports let declarations");
    const body = stmt.substring(letPrefix.length, stmt.length - 1).trim();
    const { name, typeName, value } = parseLetBody(body);
    if (!isValidIdentifier(name)) throw new Error("Invalid identifier");

    if (typeName) results.push(compileTypedDeclaration(name, typeName, value));
    else results.push(compileUntypedDeclaration(name, value));
  }
  return results;
}

function compileTypedDeclaration(name: string, typeName: string, value: string): DeclResult {
  if (supportedTypes.indexOf(typeName) === -1) throw new Error("Unsupported type");
  if (typeName[0] === 'I' || typeName[0] === 'U') return compileIntegerTyped(name, typeName, value);
  if (typeName === "F32" || typeName === "F64") return compileFloatTyped(name, typeName, value);
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
  return { text: `${cType} ${name} = ${plainValue};`, usesStdint: true };
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
  return { text: `${floatMap[typeName]} ${name} = ${plainValue};`, usesStdint: false };
}

function compileUntypedDeclaration(name: string, value: string): DeclResult {
  let inferred = "I32";
  // If value has a float suffix, infer from it.
  const kind = numericKind(value);
  if (kind.kind === 'float') {
    if (kind.suffix.length !== 0) {
      // If suffix matches known float types, infer accordingly.
      if (kind.suffix === 'F32' || kind.suffix === 'F64') inferred = kind.suffix;
      else inferred = 'F32';
      // strip suffix for output
      value = value.substring(0, value.length - kind.suffix.length);
    } else {
      inferred = 'F32';
    }
  }
  const outType = typeMap[inferred] || typeMap["I32"] || "int32_t";
  if (inferred[0] === 'I' || inferred[0] === 'U') {
    return { text: `${outType} ${name} = ${value};`, usesStdint: true };
  }
  return { text: `${outType} ${name} = ${value};`, usesStdint: false };
}
