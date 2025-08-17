// Helper: check boolean literal
function isBoolLiteral(v: string) {
  return v === 'true' || v === 'false';
}

// Helper: check simple float literal (contains a dot)
function isFloatLiteral(v: string) {
  return /^[0-9]*\.[0-9]+$/.test(v);
}

// Helper: match float literal suffixes like 0.0F32 or 1.23f64
function matchFloatSuffix(v: string) {
  const m = v.match(/^(?<num>[0-9]*\.[0-9]+)(?<suf>[fF](?:32|64))$/);
  return m && m.groups ? { num: m.groups['num'], suf: m.groups['suf'] } : null;
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

  // Split input into individual `let` declarations by semicolon.
  // Keep simple: split, trim, drop empty, and reappend semicolon for parsing.
  const parts = input
    .split(';')
    .map((p) => p.trim())
    .filter((p) => p.length > 0)
    .map((p) => p + ';');

  if (parts.length === 0) return '';

  const decls: string[] = [];
  const includes = new Set<string>();
  const vars = new Map<string, { mutable: boolean; kind: string; bits?: string; signed?: boolean }>();

  // helper: determine kind of a literal or identifier
  function detectRhsKind(token: string): { kind: string; bits?: string; signed?: boolean } {
    const t = token.trim();
    if (isBoolLiteral(t)) return { kind: 'bool' };
    const fLit = matchFloatSuffix(t);
    if (fLit) {
      return { kind: fLit.suf.toUpperCase() === 'F32' ? 'float' : 'double' };
    }
    if (isFloatLiteral(t)) return { kind: 'float' };
    const intSuf = matchIntSuffix(t);
    if (intSuf) {
      const suf = intSuf.suf;
      const kind = suf[0].toUpperCase();
      const bits = suf.slice(1);
      return { kind: kind === 'I' ? 'int' : 'uint', bits, signed: kind === 'I' };
    }
    // identifier: if it's a known var, return its kind
    if (/^[a-zA-Z_$][a-zA-Z0-9_$]*$/.test(t) && vars.has(t)) {
      const v = vars.get(t)!;
      return { kind: v.kind, bits: v.bits, signed: v.signed };
    }
    // default integer literal
    if (/^[0-9]+$/.test(t)) return { kind: 'int', bits: '32', signed: true };
    // fallback
    return { kind: 'unknown' };
  }

  for (const letDecl of parts) {
    // If it's a plain assignment like `x = 100;`, enforce mutability and type checking
    const assignMatch = /^([a-zA-Z_$][a-zA-Z0-9_$]*)\s*=\s*(.+);$/.exec(letDecl);
    if (assignMatch && !/^let\b/.test(letDecl)) {
      const vname = assignMatch[1];
      const rhs = assignMatch[2].trim();
      if (!vars.has(vname)) throw new Error(`Assignment to undeclared variable ${vname}`);
      const vinfo = vars.get(vname)!;
      if (!vinfo.mutable) throw new Error(`Cannot assign to immutable variable ${vname}`);

      const rhsInfo = detectRhsKind(rhs);
      // if variable is integerish, reject float/double rhs
      if ((vinfo.kind === 'int' || vinfo.kind === 'uint') && (rhsInfo.kind === 'float' || rhsInfo.kind === 'double')) {
        throw new Error(`Type mismatch assigning ${rhsInfo.kind} to ${vinfo.kind}`);
      }
      // reject boolean assigned to integer types
      if ((vinfo.kind === 'int' || vinfo.kind === 'uint') && rhsInfo.kind === 'bool') {
        throw new Error(`Type mismatch assigning ${rhsInfo.kind} to ${vinfo.kind}`);
      }
      // if variable is bool, require bool
      if (vinfo.kind === 'bool' && rhsInfo.kind !== 'bool') throw new Error(`Type mismatch assigning to bool ${vname}`);
      // floats accept integers and floats

      decls.push(`${vname} = ${rhs};`);
      continue;
    }

    // Support optional `mut`: `let` or `let mut`
    const match = /^let(\s+mut)?\s+([a-zA-Z_$][a-zA-Z0-9_$]*)\s*(?::\s*(([IU](?:8|16|32|64))|([fF](?:32|64))|[bB]ool)\s*)?=\s*(.+);$/.exec(
      letDecl
    );
    if (!match) throw new Error('This function always throws');
    const isMut = !!match[1];
    const name = match[2];
    const typeToken = match[3];
    const rawValue = match[match.length - 1];
    let value = rawValue.trim();


    // Handle booleans
    if (!typeToken && isBoolLiteral(value)) {
      includes.add('stdbool');
      vars.set(name, { mutable: isMut, kind: 'bool' });
      decls.push(`bool ${name} = ${value};`);
      continue;
    }

    if (typeToken) {
      const lower = typeToken.toLowerCase();
      if (lower === 'bool') {
        if (isBoolLiteral(value)) {
          includes.add('stdbool');
          vars.set(name, { mutable: isMut, kind: 'bool' });
          decls.push(`bool ${name} = ${value};`);
          continue;
        }
        throw new Error('Type annotation Bool requires boolean literal');
      }
      if (isBoolLiteral(value)) throw new Error('Type annotation and boolean literal mismatch');

      // Float annotations
      if (lower.startsWith('f')) {
        // reject integer-suffixed literals for float annotation
        if (matchIntSuffix(value)) throw new Error('Type annotation float and integer literal suffix mismatch');
        const fbits = lower.slice(1);
        const fType = fbits === '32' ? 'float' : 'double';
        vars.set(name, { mutable: isMut, kind: fType });
        decls.push(`${fType} ${name} = ${value};`);
        continue;
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
        includes.add('stdint');
        vars.set(name, { mutable: isMut, kind: cType.startsWith('uint') ? 'uint' : 'int', bits });
        decls.push(`${cType} ${name} = ${value};`);
        continue;
      } else {
        value = stripIntSuffix(value);
        const bits = typeToken.slice(1);
        includes.add('stdint');
        vars.set(name, { mutable: isMut, kind: cType.startsWith('uint') ? 'uint' : 'int', bits });
        decls.push(`${cType} ${name} = ${value};`);
        continue;
      }
      continue;
    }

    // No annotation: float literal
    if (!typeToken) {
      const fLit = matchFloatSuffix(value);
      if (fLit) {
        const fType = fLit.suf[0].toLowerCase() === 'f' && fLit.suf.slice(1) === '32' ? 'float' : 'double';
        vars.set(name, { mutable: isMut, kind: fType });
        decls.push(`${fType} ${name} = ${fLit.num};`);
        continue;
      }
      if (isFloatLiteral(value)) {
        decls.push(`float ${name} = ${value};`);
        continue;
      }
    }

    // No annotation: integer literal maybe with suffix
    const lit = matchIntSuffix(value);
    if (lit) {
      const suf = lit.suf;
      const kind = suf[0].toUpperCase();
      const bits = suf.slice(1);
      const cType = kind === 'I' ? `int${bits}_t` : `uint${bits}_t`;
      includes.add('stdint');
      vars.set(name, { mutable: isMut, kind: cType.startsWith('uint') ? 'uint' : 'int', bits });
      decls.push(`${cType} ${name} = ${lit.num};`);
      continue;
    }

    // Default: int32_t
    includes.add('stdint');
    vars.set(name, { mutable: isMut, kind: 'int', bits: '32', signed: true });
    decls.push(`int32_t ${name} = ${value};`);
  }

  // Build includes header lines (consistent order)
  const includeLines: string[] = [];
  if (includes.has('stdint')) includeLines.push('#include <stdint.h>');
  if (includes.has('stdbool')) includeLines.push('#include <stdbool.h>');

  if (includeLines.length > 0) {
    return includeLines.join('\r\n') + '\r\n' + decls.join('\r\n');
  }

  return decls.join('\r\n');
}
