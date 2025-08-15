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
    const m = t.match(/^let\s+([A-Za-z_][A-Za-z0-9_]*)\s*(?::\s*([A-Za-z0-9_]+))?\s*=\s*([^\s]+)\s*$/);
    if (!m) return null;
    const [, name, typ, value] = m;
    let cType = 'int32_t';
    if (typ) {
      const mapped = mapType(typ);
      if (!mapped) return null;
      cType = mapped;
    }
    let rhs = value;
    // literal suffix handling: e.g., `0U8`
  const suff = value.match(/^([+-]?\d+)([A-Za-z_][A-Za-z0-9_]*)$/);
    if (suff) {
      rhs = suff[1];
      const mapped = mapType(suff[2]);
      if (mapped) {
        if (typ && typ !== suff[2]) return null;
        cType = mapped;
      }
    }
    // identifier RHS
    if (/^[A-Za-z_][A-Za-z0-9_]*$/.test(rhs) && env.has(rhs)) {
      cType = env.get(rhs)!;
    }
    env.set(name, cType);
    if (cType === 'bool') anyBool = true;
    decls.push(`${cType} ${name} = ${rhs};`);
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
