// Helper: check boolean literal
function isBoolLiteral(v: string) {
  return v === 'true' || v === 'false';
}

// Helper: check simple float literal (contains a dot)
function isFloatLiteral(v: string) {
  return /^[0-9]*\.[0-9]+$/.test(v);
}

// Helper: match integer literal suffixes like 123I32 or 456U8
function matchIntSuffix(v: string) {
  const m = v.match(/^(?<num>[0-9]+)(?<suf>[iIuU](?:8|16|32|64))$/);
  return m && m.groups ? { num: m.groups['num'], suf: m.groups['suf'] } : null;
}

// Helper: strip accidental integer suffix
function stripIntSuffix(v: string) {
  return v.replace(/([0-9]+)(?:[iIuU](?:8|16|32|64))$/, '$1');
}

// Map integer annotation token like I32/U16 to C type
function mapIntTokenToC(typeToken: string) {
  const kind = typeToken[0].toUpperCase();
  const bits = typeToken.slice(1);
  return kind === 'I' ? `int${bits}_t` : `uint${bits}_t`;
}

export default function alwaysThrows(input: string): string {
  if (input === '') return '';

  const letDecl = input.trim();
  const match = /^let\s+([a-zA-Z_$][a-zA-Z0-9_$]*)\s*(?::\s*(([IU](?:8|16|32|64))|([fF](?:32|64))|[bB]ool)\s*)?=\s*(.+);$/.exec(letDecl);
  if (!match) throw new Error('This function always throws');

  const name = match[1];
  const typeToken = match[2];
  const rawValue = match[match.length - 1];
  let value = rawValue.trim();

  // Handle booleans
  if (!typeToken && isBoolLiteral(value)) {
    return `#include <stdbool.h>\r\nbool ${name} = ${value};`;
  }

  if (typeToken) {
    const lower = typeToken.toLowerCase();
    if (lower === 'bool') {
      if (isBoolLiteral(value)) return `#include <stdbool.h>\r\nbool ${name} = ${value};`;
      throw new Error('Type annotation Bool requires boolean literal');
    }
    if (isBoolLiteral(value)) throw new Error('Type annotation and boolean literal mismatch');

    // Float annotations
    if (lower.startsWith('f')) {
      // reject integer-suffixed literals for float annotation
      if (matchIntSuffix(value)) throw new Error('Type annotation float and integer literal suffix mismatch');
      const fbits = lower.slice(1);
      const fType = fbits === '32' ? 'float' : 'double';
      return `${fType} ${name} = ${value};`;
    }

    // Integer annotations: map token and validate/strip suffix
    const cType = mapIntTokenToC(typeToken);
    const lit = matchIntSuffix(value);
    if (lit) {
      const suf = lit.suf;
      const sufKind = suf[0].toUpperCase();
      const sufBits = suf.slice(1);
      const kind = typeToken[0].toUpperCase();
      const bits = typeToken.slice(1);
      if (sufKind !== kind || sufBits !== bits) throw new Error('Type annotation and literal suffix mismatch');
      value = lit.num;
    } else {
      value = stripIntSuffix(value);
    }

    return `#include <stdint.h>\r\n${cType} ${name} = ${value};`;
  }

  // No annotation: float literal
  if (isFloatLiteral(value)) return `float ${name} = ${value};`;

  // No annotation: integer literal maybe with suffix
  const lit = matchIntSuffix(value);
  if (lit) {
    const suf = lit.suf;
    const kind = suf[0].toUpperCase();
    const bits = suf.slice(1);
    const cType = kind === 'I' ? `int${bits}_t` : `uint${bits}_t`;
    return `#include <stdint.h>\r\n${cType} ${name} = ${lit.num};`;
  }

  // Default: int32_t
  return `#include <stdint.h>\r\nint32_t ${name} = ${value};`;
}
