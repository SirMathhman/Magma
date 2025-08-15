export type COutput = string | null;

export function compile(src: string): COutput {
  if (src == null) return '';
  const trimmed = src.trim();
  if (trimmed.length === 0) return '';

  // very small parser: support `let x : I32 = 0;` and simple extensions
  const toks = trimmed.split(/;+/).map(s => s.trim()).filter(Boolean);
  const decls: string[] = [];
  let anyBool = false;
  const env = new Map<string, string>();

  for (const t of toks) {
  const parsed = parseDeclarationToken(t, env);
  if (!parsed) return null;
  decls.push(parsed.decl);
  env.set(parsed.name, parsed.cType);
  if (parsed.isBool) anyBool = true;
  }

  const header = anyBool ? '#include <stdbool.h>' : '#include <stdint.h>';
  return header + '\r\n' + decls.join('\r\n');
}

function mapType(tok: string): string | null {
  const map: Record<string, string> = {
    Bool: 'bool',
    I8: 'int8_t',
    I16: 'int16_t',
    I32: 'int32_t',
    I64: 'int64_t',
    U8: 'uint8_t',
    U16: 'uint16_t',
    U32: 'uint32_t',
    U64: 'uint64_t'
  };
  return map[tok] ?? null;
}

type ParsedDecl = { decl: string; name: string; cType: string; isBool: boolean } | null;

function parseDeclarationToken(t: string, env: Map<string, string>): ParsedDecl {
  const m = t.match(/^let\s+([A-Za-z_][A-Za-z0-9_]*)\s*(?::\s*([A-Za-z0-9_]+))?\s*=\s*([^\s]+)\s*$/);
  if (!m) return null;
  const [, name, typ, value] = m;

  let cType = 'int32_t';
  if (typ) {
    const mapped = mapType(typ);
    if (!mapped) return null;
    cType = mapped;
  }

  // try patterns in small helpers
  const suffRes = parseSuffixedLiteral(value, typ);
  if (suffRes) {
    return { decl: `${suffRes.cType} ${name} = ${suffRes.literal};`, name, cType: suffRes.cType, isBool: suffRes.cType === 'bool' };
  }

  const boolRes = parseBooleanLiteral(value, typ);
  if (boolRes) {
    return { decl: `${boolRes.cType} ${name} = ${value};`, name, cType: boolRes.cType, isBool: true };
  }

  const idRes = parseIdentifierRHS(value, env);
  if (idRes) {
    return { decl: `${idRes.refType} ${name} = ${value};`, name, cType: idRes.refType, isBool: idRes.refType === 'bool' };
  }

  const numRes = parsePlainNumber(value);
  if (numRes) {
    return { decl: `${cType} ${name} = ${value};`, name, cType, isBool: cType === 'bool' };
  }

  return null;
}

function parseSuffixedLiteral(value: string, typ?: string) {
  const suff = value.match(/^([+-]?\d+)([A-Za-z_][A-Za-z0-9_]*)$/);
  if (!suff) return null;
  const literal = suff[1];
  const suffix = suff[2];
  const mapped = mapType(suffix);
  if (!mapped) return null;
  if (typ && typ !== suffix) return null;
  return { literal, cType: mapped };
}

function parseBooleanLiteral(value: string, typ?: string) {
  if (value === 'true' || value === 'false') {
    if (typ && typ !== 'Bool') return null;
    return { cType: 'bool' };
  }
  return null;
}

function parseIdentifierRHS(value: string, env: Map<string, string>) {
  if (/^[A-Za-z_][A-Za-z0-9_]*$/.test(value) && env.has(value)) {
    return { refType: env.get(value)! };
  }
  return null;
}

function parsePlainNumber(value: string) {
  return /^[+-]?\d+$/.test(value) ? { ok: true } : null;
}
