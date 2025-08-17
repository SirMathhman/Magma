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
  // pass through bare braces as a special-case
  if (input === '{}') return '{}';
  // (no explicit passthrough for multiple brace layers; handled by general braced formatting)
  // support multiple top-level braced layers but only strip when the first '{' matches the final '}'
  let bracedDepth = 0;
  while (input.startsWith('{')) {
    // find matching closing brace for the first opening
    let depthScan = 0;
    let matchIdx = -1;
    for (let i = 0; i < input.length; i++) {
      const ch = input[i];
      if (ch === '{') depthScan++;
      else if (ch === '}') depthScan--;
      if (depthScan === 0) {
        matchIdx = i;
        break;
      }
    }
    if (matchIdx === input.length - 1) {
      // outermost braces enclose entire string — strip one layer
      input = input.slice(1, -1);
      bracedDepth++;
      if (input.length === 0) break;
      continue;
    }
    break;
  }

  // Split input into top-level statements by semicolon but ignore semicolons inside brackets or quotes.
  function splitTopLevel(src: string) {
    const out: string[] = [];
    let buf = '';
    let depth = 0;
    let inSingle = false;
    let inDouble = false;
    let escape = false;
    for (let i = 0; i < src.length; i++) {
      const ch = src[i];
      if (escape) {
        buf += ch;
        escape = false;
        continue;
      }
      if ((inSingle || inDouble) && ch === '\\\\') {
        buf += ch;
        escape = true;
        continue;
      }
      if (ch === "'" && !inDouble) {
        inSingle = !inSingle;
        buf += ch;
        continue;
      }
      if (ch === '"' && !inSingle) {
        inDouble = !inDouble;
        buf += ch;
        continue;
      }
      if (!inSingle && !inDouble) {
        if (ch === '[' || ch === '(' || ch === '{') {
          depth++;
          buf += ch;
          continue;
        }
        if (ch === ']' || ch === ')') {
          depth = Math.max(0, depth - 1);
          buf += ch;
          continue;
        }
        if (ch === '}') {
          depth = Math.max(0, depth - 1);
          buf += ch;
          // If we just closed a top-level curly (depth === 0), and the next non-space char is not a semicolon,
          // treat this as a statement boundary so brace-only statements at the front split correctly.
          if (depth === 0) {
            // look ahead for next non-space (skip all whitespace)
            let j = i + 1;
            while (j < src.length && /\s/.test(src[j])) j++;
            const next = j < src.length ? src[j] : null;
            if (next && next !== ';') {
              const t = buf.trim();
              if (t.length > 0) out.push(t + ';');
              buf = '';
              continue;
            }
          }
          continue;
        }
        if (ch === ';' && depth === 0) {
          const t = buf.trim();
          if (t.length > 0) out.push(t + ';');
          buf = '';
          continue;
        }
      }
      buf += ch;
    }
    const t = buf.trim();
    if (t.length > 0) out.push(t.endsWith(';') ? t : t + ';');
    return out;
  }

  const parts = splitTopLevel(input);

  // If nothing parsed but we had outer braces, continue to emit empty braced blocks.
  if (parts.length === 0 && bracedDepth === 0) return '';

  const decls: string[] = [];
  const includes = new Set<string>();
  const vars = new Map<string, { mutable: boolean; kind: string; bits?: string; signed?: boolean }>();

  // helpers to emit declarations and record variable info
  function emitStdInt(cType: string, varKind: string, bits: string | undefined, mutable: boolean, name: string, value: string) {
    includes.add('stdint');
    vars.set(name, { mutable, kind: varKind, bits, signed: varKind === 'int' });
    decls.push(`${cType} ${name} = ${value};`);
  }

  function emitBool(name: string, value: string, mutable: boolean) {
    includes.add('stdbool');
    vars.set(name, { mutable, kind: 'bool' });
    decls.push(`bool ${name} = ${value};`);
  }

  function emitFloat(name: string, fType: string, value: string, mutable: boolean) {
    vars.set(name, { mutable, kind: fType });
    decls.push(`${fType} ${name} = ${value};`);
  }

  // helper: determine kind of a literal or identifier
  function detectRhsKind(token: string): { kind: string; bits?: string; signed?: boolean } {
    const t = token.trim();
    // char literal 'a'
    if (/^'.'$/.test(t)) return { kind: 'char', bits: '8', signed: false };
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
    // If the top-level statement is just a brace-only block like '{}' or '{{}}', passthrough unchanged (strip trailing semicolon)
    if (/^\{[\s\S]*\};?$/.test(letDecl)) {
      decls.push(letDecl.replace(/;$/, ''));
      continue;
    }
    // Handle array annotation: let x : [U8; 3] = [1, 2, 3];
    const arrayMatch = /^let(\s+mut)?\s+([a-zA-Z_$][a-zA-Z0-9_$]*)\s*:\s*\[\s*(([IUuFf][0-9A-Za-z]*)|[bB]ool)\s*;\s*([0-9]+)\s*\]\s*=\s*\[\s*(.*)\s*\];$/.exec(
      letDecl
    );
    if (arrayMatch) {
      const isMut = !!arrayMatch[1];
      const name = arrayMatch[2];
      const elemType = arrayMatch[3];
      const len = arrayMatch[5];
      const elemsRaw = arrayMatch[6];
      const elems = elemsRaw.split(',').map((s) => s.trim()).filter((s) => s.length > 0);
      // support U8, Bool and float arrays
      if (/^U8$/i.test(elemType)) {
        const elemsJoined = elems.join(', ');
        emitStdInt('uint8_t', 'uint', '8', isMut, name, `{${elemsJoined}}`);
        // replace last emitted declaration to array form
        decls.pop();
        decls.push(`uint8_t ${name}[${len}] = {${elemsJoined}};`);
        continue;
      }
      // Float typed arrays like F32/F64
      if (/^[fF](?:32|64)$/.test(elemType)) {
        const fType = elemType[0].toLowerCase() === 'f' && elemType.slice(1) === '32' ? 'float' : 'double';
        // allow empty initializer only when len == 0
        if (elems.length === 0) {
          if (Number(len) !== 0) throw new Error('Array length and initializer size mismatch');
          vars.set(name, { mutable: isMut, kind: fType });
          decls.push(`${fType} ${name}[${len}] = {};`);
          continue;
        }
        // Ensure all elements are numeric (int or float)
        for (const e of elems) {
          const k = detectRhsKind(e).kind;
          if (!(k === 'int' || k === 'uint' || k === 'float' || k === 'double')) throw new Error('Float array initializer must contain only numeric literals');
        }
        decls.push(`${fType} ${name}[${len}] = {${elems.join(', ')}};`);
        continue;
      }
      if (/^bool$/i.test(elemType)) {
        // allow empty initializer only when length is 0
        if (elems.length === 0) {
          if (Number(len) !== 0) throw new Error('Array length and initializer size mismatch');
          includes.add('stdbool');
          vars.set(name, { mutable: isMut, kind: 'bool' });
          decls.push(`bool ${name}[${len}] = {};`);
          continue;
        }
        // otherwise ensure all elements are boolean literals
        if (!elems.every(isBoolLiteral)) throw new Error('Bool array initializer must contain only boolean literals');
        includes.add('stdbool');
        vars.set(name, { mutable: isMut, kind: 'bool' });
        decls.push(`bool ${name}[${len}] = {${elems.join(', ')}};`);
        continue;
      }
      throw new Error('Only U8, Bool and Float arrays supported');
      continue;
    }
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
      emitBool(name, value, isMut);
      continue;
    }

    if (typeToken) {
      const lower = typeToken.toLowerCase();
      if (lower === 'bool') {
        if (isBoolLiteral(value)) {
          emitBool(name, value, isMut);
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
        emitFloat(name, fType, value, isMut);
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
        emitStdInt(cType, cType.startsWith('uint') ? 'uint' : 'int', bits, isMut, name, value);
        continue;
      } else {
        value = stripIntSuffix(value);
        const bits = typeToken.slice(1);
        // reject char literal when an integer annotation is present
        if (/^'.'$/.test(value)) throw new Error('Type annotation integer cannot be initialized with char literal');
        emitStdInt(cType, cType.startsWith('uint') ? 'uint' : 'int', bits, isMut, name, value);
        continue;
      }
    }

    // No annotation: float literal
    if (!typeToken) {
      const fLit = matchFloatSuffix(value);
      if (fLit) {
        const fType = fLit.suf[0].toLowerCase() === 'f' && fLit.suf.slice(1) === '32' ? 'float' : 'double';
        emitFloat(name, fType, fLit.num, isMut);
        continue;
      }
      if (isFloatLiteral(value)) {
        decls.push(`float ${name} = ${value};`);
        continue;
      }
      // char literal
      if (/^'.'$/.test(value)) {
        includes.add('stdint');
        vars.set(name, { mutable: isMut, kind: 'uint', bits: '8', signed: false });
        decls.push(`uint8_t ${name} = ${value};`);
        continue;
      }
      // array literal like [1, 2, 3]
      const arrLit = /^\[\s*(.*)\s*\]$/.exec(value);
      if (arrLit) {
        const elemsRaw = arrLit[1];
        const elems = elemsRaw.split(',').map((s) => s.trim()).filter((s) => s.length > 0);
        if (elems.length === 0) throw new Error('Empty array literals are not supported');
        // Determine kinds for each element
        const kinds = elems.map((e) => detectRhsKind(e).kind);
        const uniqueKinds = Array.from(new Set(kinds));
        // All bools -> bool array
        if (uniqueKinds.length === 1 && uniqueKinds[0] === 'bool') {
          includes.add('stdbool');
          vars.set(name, { mutable: isMut, kind: 'bool' });
          decls.push(`bool ${name}[${elems.length}] = {${elems.join(', ')}};`);
          continue;
        }
        // All integers (possibly with suffixes) -> int32_t by default
        const intLike = uniqueKinds.every((k) => k === 'int' || k === 'uint');
        if (intLike) {
          includes.add('stdint');
          vars.set(name, { mutable: isMut, kind: 'int', bits: '32', signed: true });
          decls.push(`int32_t ${name}[${elems.length}] = {${elems.join(', ')}};`);
          continue;
        }
        // All floats -> float array (uncommon, but handle)
        if (uniqueKinds.length === 1 && (uniqueKinds[0] === 'float' || uniqueKinds[0] === 'double')) {
          const ftype = uniqueKinds[0] === 'float' ? 'float' : 'double';
          vars.set(name, { mutable: isMut, kind: ftype });
          decls.push(`${ftype} ${name}[${elems.length}] = {${elems.join(', ')}};`);
          continue;
        }
        // Mixed or unsupported element kinds -> error
        throw new Error('Mixed or unsupported element types in array literal');
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
      emitStdInt(cType, cType.startsWith('uint') ? 'uint' : 'int', bits, isMut, name, lit.num);
      continue;
    }

    // Default: int32_t
    emitStdInt('int32_t', 'int', '32', isMut, name, value);
  }

  // Build includes header lines (consistent order)
  const includeLines: string[] = [];
  if (includes.has('stdint')) includeLines.push('#include <stdint.h>');
  if (includes.has('stdbool')) includeLines.push('#include <stdbool.h>');

  const body = decls.join('\r\n');
  const header = includeLines.length > 0 ? includeLines.join('\r\n') + '\r\n' : '';
  if (bracedDepth > 0) {
    if (body.length === 0) {
      // produce nested empty braces where the innermost is '{}' on one line
      function nestedEmpty(d: number): string[] {
        if (d === 1) return ['{}'];
        const inner = nestedEmpty(d - 1);
        return ['{', ...inner.map((l) => '\t' + l), '}'];
      }
      const blockLines = nestedEmpty(bracedDepth);
      return header + blockLines.join('\r\n');
    }
    const lines = body.split('\r\n');
    let blockLines = lines;
    for (let i = 0; i < bracedDepth; i++) {
      blockLines = ['{', ...blockLines.map((l) => '\t' + l), '}'];
    }
    const wrapped = blockLines.join('\r\n');
    return header + wrapped;
  }
  return header + body;
}
