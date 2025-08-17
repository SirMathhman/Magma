// Helper: check boolean literal
function isBoolLiteral(v: string) {
  return v === 'true' || v === 'false';
}

// Helper: check simple float literal (contains a dot)
function isFloatLiteral(v: string) {
  if (v.length === 0) return false;
  let dotIndex = -1;
  for (let i = 0; i < v.length; i++) {
    const ch = v[i];
    if (ch === '.') {
      if (dotIndex !== -1) return false; // more than one dot
      dotIndex = i;
      continue;
    }
    if (ch < '0' || ch > '9') return false; // non-digit
  }
  // must contain exactly one dot and not end with dot (require at least one digit after dot)
  return dotIndex !== -1 && dotIndex !== v.length - 1;
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

// Helper: normalize numeric literal suffixes inside an expression
function normalizeNumericSuffixes(s: string) {
  return s.replace(/([0-9]*\.[0-9]+)[fF](?:32|64)/g, '$1').replace(/([0-9]+)(?:[iIuU](?:8|16|32|64))/g, '$1');
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

  // Remove single-line comments (// ...) that occur outside of string/char literals
  function removeSingleLineComments(src: string) {
    let out = '';
    let inSingle = false;
    let inDouble = false;
    let escape = false;
    for (let i = 0; i < src.length; i++) {
      const ch = src[i];
      if (escape) {
        out += ch;
        escape = false;
        continue;
      }
      if ((inSingle || inDouble) && ch === '\\') {
        out += ch;
        escape = true;
        continue;
      }
      if (ch === "'" && !inDouble) {
        inSingle = !inSingle;
        out += ch;
        continue;
      }
      if (ch === '"' && !inSingle) {
        inDouble = !inDouble;
        out += ch;
        continue;
      }
      // if we see // and we're not inside a string/char, skip until end of line
      if (!inSingle && !inDouble && ch === '/' && i + 1 < src.length && src[i + 1] === '/') {
        // advance i to the end of line or end of string
        i += 2;
        while (i < src.length && src[i] !== '\n') i++;
        // if there's a newline, keep it so line structure remains
        if (i < src.length && src[i] === '\n') {
          out += '\n';
        }
        continue;
      }
      out += ch;
    }
    return out;
  }
  // Remove block comments /* ... */ (allow any content inside) outside of string/char literals
  function removeBlockComments(src: string) {
    let out = '';
    let inSingle = false;
    let inDouble = false;
    let escape = false;
    for (let i = 0; i < src.length; i++) {
      const ch = src[i];
      if (escape) {
        out += ch;
        escape = false;
        continue;
      }
      if ((inSingle || inDouble) && ch === '\\') {
        out += ch;
        escape = true;
        continue;
      }
      if (ch === "'" && !inDouble) {
        inSingle = !inSingle;
        out += ch;
        continue;
      }
      if (ch === '"' && !inSingle) {
        inDouble = !inDouble;
        out += ch;
        continue;
      }
      // detect block comment start when not in a string/char
      if (!inSingle && !inDouble && ch === '/' && i + 1 < src.length && src[i + 1] === '*') {
        // consume until closing */ or EOF, preserving newline count
        let j = i + 2;
        let newlines = 0;
        while (j < src.length && !(src[j] === '*' && j + 1 < src.length && src[j + 1] === '/')) {
          if (src[j] === '\n') newlines++;
          j++;
        }
        if (j < src.length) {
          // skip the closing '*/'
          j += 2;
        }
        // preserve the same number of newline characters so line structure remains
        if (newlines > 0) out += '\n'.repeat(newlines);
        i = j - 1;
        continue;
      }
      out += ch;
    }
    return out;
  }

  input = removeBlockComments(input);
  input = removeSingleLineComments(input).trim();
  const parts = splitTopLevel(input);

  // If nothing parsed but we had outer braces, continue to emit empty braced blocks.
  if (parts.length === 0 && bracedDepth === 0) return '';

  const decls: string[] = [];
  const includes = new Set<string>();
  const vars = new Map<string, { mutable: boolean; kind: string; bits?: string; signed?: boolean }>();

  // split a string at top-level occurrences of any operator in `opsList`
  function splitTopLevelOperators(src: string, opsList: string[]): { parts: string[]; ops: string[] } {
    const parts: string[] = [];
    const ops: string[] = [];
    let buf = '';
    let depth = 0;
    let inSingle = false;
    let inDouble = false;
    let escape = false;
    for (let i = 0; i < src.length; i++) {
      const ch = src[i];
      if (escape) { buf += ch; escape = false; continue; }
      if ((inSingle || inDouble) && ch === '\\') { buf += ch; escape = true; continue; }
      if (ch === "'" && !inDouble) { inSingle = !inSingle; buf += ch; continue; }
      if (ch === '"' && !inSingle) { inDouble = !inDouble; buf += ch; continue; }
      if (!inSingle && !inDouble) {
        if (ch === '(' || ch === '[' || ch === '{') { depth++; buf += ch; continue; }
        if (ch === ')' || ch === ']' || ch === '}') { depth = Math.max(0, depth - 1); buf += ch; continue; }
        if (depth === 0) {
          let matched = false;
          for (const op of opsList) {
            if (src.startsWith(op, i)) {
              parts.push(buf);
              buf = '';
              ops.push(op);
              i += op.length - 1;
              matched = true;
              break;
            }
          }
          if (matched) continue;
        }
      }
      buf += ch;
    }
    parts.push(buf);
    return { parts, ops };
  }

  // Helper to emit string comparison expression and register needed includes
  function stringCompare(left: string, right: string, op: string) {
    includes.add('string');
    includes.add('stdbool');
    if (op === '==') return `strcmp(${left}, ${right}) != 0`;
    if (op === '!=') return `strcmp(${left}, ${right}) == 0`;
    throw new Error('Unsupported string comparison operator');
  }

  // Replace occurrences of `ident.length` with `strlen(ident)` when outside of string/char literals
  function replaceLengthAccess(src: string) {
    let out = '';
    let inSingle = false;
    let inDouble = false;
    let escape = false;
    for (let i = 0; i < src.length; i++) {
      const ch = src[i];
      if (escape) { out += ch; escape = false; continue; }
      if ((inSingle || inDouble) && ch === '\\') { out += ch; escape = true; continue; }
      if (ch === "'" && !inDouble) { inSingle = !inSingle; out += ch; continue; }
      if (ch === '"' && !inSingle) { inDouble = !inDouble; out += ch; continue; }
      if (!inSingle && !inDouble && /[A-Za-z_$]/.test(ch)) {
        // parse identifier
        let j = i;
        while (j < src.length && /[A-Za-z0-9_$]/.test(src[j])) j++;
        const ident = src.slice(i, j);
        if (src.startsWith('.length', j)) {
          includes.add('string');
          out += `strlen(${ident})`;
          i = j + '.length'.length - 1;
          continue;
        }
        out += ident;
        i = j - 1;
        continue;
      }
      out += ch;
    }
    return out;
  }

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
    // use shared splitTopLevelOperators defined above

    // check logical OR (||) and AND (&&) at top-level
    const logicalSplit = splitTopLevelOperators(t, ['||', '&&']);
    if (logicalSplit.ops.length > 0) {
      const ok = logicalSplit.parts.every((p) => detectRhsKind(p).kind === 'bool');
      if (ok) return { kind: 'bool' };
      // otherwise fall through
    }

    // comparison expressions return bool when both sides are numeric
    const compMatch = t.match(/^(?<l>.+?)\s*(==|!=|<=|>=|<|>)\s*(?<r>.+)$/);
    if (compMatch && compMatch.groups) {
      const leftKind = detectRhsKind(compMatch.groups['l']).kind;
      const rightKind = detectRhsKind(compMatch.groups['r']).kind;
      const numeric = (k: string) => k === 'int' || k === 'uint' || k === 'float' || k === 'double';
      if (numeric(leftKind) && numeric(rightKind)) return { kind: 'bool' };
      // string comparisons also produce bool
      if (leftKind === 'string' && rightKind === 'string') return { kind: 'bool' };
      // otherwise fall through to other checks (equality on non-numeric not supported here)
    }
    // Array indexing like ident[expr] -> return the element kind if known
    const idxMatch = t.match(/^([a-zA-Z_$][a-zA-Z0-9_$]*)\s*\[\s*(.+)\s*\]$/);
    if (idxMatch) {
      const arrName = idxMatch[1];
      const idxExpr = idxMatch[2];
      if (vars.has(arrName)) {
        const v = vars.get(arrName)!;
        // validate index is numeric
        const idxKind = detectRhsKind(idxExpr).kind;
        if (idxKind === 'int' || idxKind === 'uint') {
          // element kind is the var's kind (for arrays we stored the element kind)
          return { kind: v.kind, bits: v.bits, signed: v.signed };
        }
      }
      // otherwise fall through
    }
    // string literal "..."
    if (/^".*"$/.test(t)) return { kind: 'string' };
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

  // Process an array of top-level statement strings using the shared vars/includes/decls context
  function processParts(localParts: string[]) {
    for (const letDecl of localParts) {
      // If the top-level statement is just a brace-only block like '{...}' or '{{...}};'
      const braceOnly = /^\{([\s\S]*)\};?$/.exec(letDecl);
      if (braceOnly) {
        const inner = braceOnly[1].trim();
        if (inner.length === 0) {
          decls.push('{}');
          continue;
        }
        // If inner contains let/assignment, process its parts in the same shared context so inner can see outer vars
        if (/\blet\b|=/.test(inner)) {
          const innerParts = splitTopLevel(inner);
          // capture current decls length to collect inner outputs
          const startLen = decls.length;
          // snapshot vars keys so inner declarations don't leak
          const beforeKeys = new Set(Array.from(vars.keys()));
          processParts(innerParts);
          // remove any vars created inside the brace block (no leakage)
          for (const k of Array.from(vars.keys())) {
            if (!beforeKeys.has(k)) vars.delete(k);
          }
          const innerDecls = decls.slice(startLen);
          // remove innerDecls from decls and instead emit as a single indented block
          decls.length = startLen;
          const blockLines = ['{', ...innerDecls.map((l) => '\t' + l), '}'];
          decls.push(blockLines.join('\r\n'));
          continue;
        }
        // otherwise passthrough the brace contents as-is
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

      // Support increment/decrement statements like `x++;` or `x--;`
      const incMatch = /^([a-zA-Z_$][a-zA-Z0-9_$]*)\s*(\+\+|--)\s*;$/.exec(letDecl);
      if (incMatch && !/^let\b/.test(letDecl)) {
        const vname = incMatch[1];
        const op = incMatch[2];
        if (!vars.has(vname)) throw new Error(`Use of undeclared variable ${vname}`);
        const vinfo = vars.get(vname)!;
        if (!vinfo.mutable) throw new Error(`Cannot assign to immutable variable ${vname}`);
        decls.push(`${vname}${op};`);
        continue;
      }

      // Support simple function syntax: fn name(args) : Void => { body }
      const fnMatch = /^fn\s+([a-zA-Z_$][a-zA-Z0-9_$]*)\s*\(\s*([^)]*)\s*\)\s*(?::\s*([A-Za-z0-9_]+)\s*)?=>\s*(\{[\s\S]*\})\s*;?$/.exec(letDecl);
      if (fnMatch) {
        const name = fnMatch[1];
        const paramsRaw = fnMatch[2].trim();
        const retToken = fnMatch[3];
        const body = fnMatch[4].trim();
        // map parameter annotations to C types (String -> char*, Bool -> bool, F32/F64 -> float/double, integer tokens via mapIntTokenToC)
        const params = paramsRaw.length === 0 ? '' : paramsRaw;
        let paramList = '';
        const paramInfos: Array<{ name: string; ctype: string; kind: string; bits?: string; signed?: boolean }> = [];
        if (params.length > 0) {
          const parts = params.split(',').map((s) => s.trim()).filter(Boolean);
          const mapped: string[] = [];
          for (const part of parts) {
            const pm = part.match(/([a-zA-Z_$][a-zA-Z0-9_$]*)\s*:\s*([*]?[A-Za-z0-9_]+)/);
            if (!pm) throw new Error('Invalid parameter annotation');
            const pname = pm[1];
            const ptoken = pm[2];
            const base = ptoken.startsWith('*') ? ptoken.slice(1) : ptoken;
            let ctype = '';
            let kind = 'unknown';
            let bits: string | undefined = undefined;
            let signed = undefined as boolean | undefined;
            if (base === 'CStr' || base === 'String') {
              ctype = 'char*';
              kind = 'string';
            } else if (base === 'Bool' || base.toLowerCase() === 'bool') {
              includes.add('stdbool');
              ctype = 'bool';
              kind = 'bool';
            } else if (/^[fF](?:32|64)$/.test(base)) {
              ctype = base[0].toLowerCase() === 'f' && base.slice(1) === '32' ? 'float' : 'double';
              kind = ctype;
            } else if (/^[iuIU](?:8|16|32|64)$/.test(base)) {
              ctype = mapIntTokenToC(base);
              const k = base[0].toUpperCase();
              bits = base.slice(1);
              kind = k === 'I' ? 'int' : 'uint';
              signed = k === 'I';
            } else {
              ctype = base;
            }
            mapped.push(`${ctype} ${pname}`);
            paramInfos.push({ name: pname, ctype, kind, bits, signed });
          }
          paramList = mapped.join(', ');
        }
        // Register parameters in vars so detectRhsKind can use them when inferring return type
        const paramNames: string[] = [];
        for (const pi of paramInfos) {
          paramNames.push(pi.name);
          vars.set(pi.name, { mutable: false, kind: pi.kind, bits: pi.bits, signed: pi.signed });
        }

        // map return tokens (default void)
        let ret = 'void';
        if (retToken) {
          const low = retToken.toLowerCase();
          if (low === 'void') ret = 'void';
          else if (low === 'bool' || low === 'boolean' || low === 'booltype') {
            // support Bool -> bool
            ret = 'bool';
            includes.add('stdbool');
          } else {
            ret = retToken;
          }
        } else {
          // If no return annotation, try to infer bool when body is a single boolean return
          const inner = body.slice(1, -1).trim();
          const rm = inner.match(/^return\s+([\s\S]+);$/);
          if (rm) {
            const expr = rm[1].trim();
            if (detectRhsKind(expr).kind === 'bool') {
              ret = 'bool';
              includes.add('stdbool');
            }
          }
        }
        // If the function has an explicit Bool return annotation, validate that any `return` inside
        // is returning a boolean expression.
        if (ret === 'bool') {
          const inner = body.slice(1, -1).trim();
          const rm = inner.match(/^return\s+([\s\S]+);$/);
          if (rm) {
            const expr = rm[1].trim();
            if (detectRhsKind(expr).kind !== 'bool') throw new Error('Type annotation Bool requires boolean return');
          }
        }
        // Before emitting body, if it's a single return expression, transform top-level
        // string equality comparisons (== / !=) into strcmp(...) forms so equality
        // is handled before logical || and && (we transform each comparison, leaving
        // logical operators in place).
        function transformStringComparisonsInReturn(inner: string) {
          const mret = inner.match(/^return\s+([\s\S]+);$/);
          if (!mret) return inner;
          const expr = mret[1].trim();
          // Split expression at top-level || and && to respect operator precedence
          const sp = splitTopLevelOperators(expr, ['||', '&&']);
          const parts = sp.parts;
          const ops = sp.ops;
          const transformed = parts.map((seg) => {
            const cmp = seg.match(/^(.*?)\s*(==|!=)\s*(.*)$/);
            if (cmp) {
              const left = cmp[1].trim();
              const op = cmp[2];
              const right = cmp[3].trim();
              const lk = detectRhsKind(left).kind;
              const rk = detectRhsKind(right).kind;
              if (lk === 'string' && rk === 'string') {
                return stringCompare(left, right, op);
              }
            }
            return seg;
          });
          let out = transformed[0];
          for (let i = 0; i < ops.length; i++) out += ` ${ops[i]} ` + transformed[i + 1];
          return `return ${out};`;
        }

        // Format body: empty body stays as {} for compact form, otherwise emit indented CRLF block
        if (/^\{\s*\}$/.test(body)) {
          decls.push(`${ret} ${name}(${paramList}){}`);
        } else {
          const inner = body.slice(1, -1).trim();
          const transformedInner = transformStringComparisonsInReturn(inner);
          const lines = transformedInner.length === 0 ? [] : transformedInner.replace(/\r/g, '').split(/\n/);
          if (lines.length === 0) {
            // no inner statements after trimming -> emit compact form
            decls.push(`${ret} ${name}(${paramList}){}`);
          } else {
            const innerText = lines.map((l) => '\t' + l).join('\r\n');
            decls.push(`${ret} ${name}(${paramList}){` + '\r\n' + innerText + '\r\n' + '}');
          }
        }
        // remove param entries from vars to avoid leakage
        for (const n of paramNames) vars.delete(n);
        continue;
      }

      // Passthrough top-level if statements unchanged (e.g. if(true){}) or handle single-statement if without braces
      // Match either braced body or a single statement following the if: `if(cond) { ... }` or `if(cond) stmt;`
      const ifMatch = /^if\s*\(([^)]*)\)\s*(\{[\s\S]*\}|[^;]+;)\s*;?$/.exec(letDecl);
      if (ifMatch) {
        let condRaw = ifMatch[1].trim();
        // replace `.length` accesses before validation
        condRaw = replaceLengthAccess(condRaw);
        const bodyRaw = ifMatch[2];
        // Accept boolean literal or comparison expressions or known bool variables
        const isBoolCond = () => {
          if (isBoolLiteral(condRaw)) return true;
          if (/^[a-zA-Z_$][a-zA-Z0-9_$]*$/.test(condRaw) && vars.has(condRaw) && vars.get(condRaw)!.kind === 'bool') return true;
          // comparison expression
          if (/^(.+?)\s*(==|!=|<=|>=|<|>)\s*(.+)$/.test(condRaw)) return true;
          return false;
        };
        if (!isBoolCond()) throw new Error('If condition must be boolean');

        // If bodyRaw is a braced block, passthrough unchanged (but with transformed condition)
        if (/^\{[\s\S]*\}$/.test(bodyRaw)) {
          decls.push(`if(${condRaw})${bodyRaw}`);
          continue;
        }

        // Otherwise bodyRaw is a single top-level statement ending with ';'. Process it similarly to how
        // top-level statements are processed so that `let` declarations inside become proper C-decls.
        const stmt = bodyRaw.trim();
        // Remove final semicolon for processing functions that expect statements with trailing semicolons
        const toProcess = stmt.endsWith(';') ? stmt : stmt + ';';

        // Capture current state so we can collect generated inner decls and avoid leaking vars declared inside
        const startLen = decls.length;
        const beforeKeys = new Set(Array.from(vars.keys()));

        // Process the single statement in the shared context by calling processParts on an array with the stmt
        processParts([toProcess]);

        // Collect inner decls and remove any vars created inside (no leakage)
        const innerDecls = decls.slice(startLen);
        for (const k of Array.from(vars.keys())) {
          if (!beforeKeys.has(k)) vars.delete(k);
        }
        // Remove innerDecls from decls
        decls.length = startLen;

        // If innerDecls is empty, emit empty block; otherwise indent inner lines and emit a braced block
        if (innerDecls.length === 0) {
          decls.push(`if(${condRaw}){}`);
        } else {
          const blockLines = ['if(' + condRaw + '){', ...innerDecls.map((l) => '\t' + l), '}'];
          decls.push(blockLines.join('\r\n'));
        }
        continue;
      }

      // Handle for loops with potential let initializer, e.g. for(let mut x = 0; x < 10; x){}
      const forMatch = /^for\s*\(([^;]*)\s*;\s*([^;]*)\s*;\s*([^\)]*)\)\s*(\{[\s\S]*\})\s*;?$/.exec(letDecl);
      if (forMatch) {
        const initRaw = forMatch[1].trim();
        const cond = forMatch[2].trim();
        const post = forMatch[3].trim();
        const bodyRaw = forMatch[4];
        // If initializer is a let declaration, compile its type to a C declaration inside the for header
        if (/^let\b/.test(initRaw)) {
          // parse let without trailing semicolon
          const letInitMatch = /^let(\s+mut)?\s+([a-zA-Z_$][a-zA-Z0-9_$]*)\s*(?::\s*(([IU](?:8|16|32|64))|([fF](?:32|64))|[bB]ool)\s*)?=\s*(.+)$/.exec(initRaw);
          if (!letInitMatch) throw new Error('Invalid for-let initializer');
          const isMutInit = !!letInitMatch[1];
          const iname = letInitMatch[2];
          const itypeToken = letInitMatch[3];
          let ivalue = letInitMatch[letInitMatch.length - 1].trim();
          // determine C type for initializer
          if (itypeToken) {
            const low = itypeToken.toLowerCase();
            if (low.startsWith('f')) {
              const ftype = low.slice(1) === '32' ? 'float' : 'double';
              // emit header line and for header with float init
              const forInit = `${ftype} ${iname} = ${ivalue}`;
              if (ftype === 'float') {
                decls.push(`for(${forInit}; ${cond}; ${post})${bodyRaw}`.replace(/^/, '').trim());
              } else {
                decls.push(`for(${forInit}; ${cond}; ${post})${bodyRaw}`);
              }
            } else if (/^[iuIU](?:8|16|32|64)$/.test(itypeToken)) {
              const cType = mapIntTokenToC(itypeToken);
              includes.add('stdint');
              // If the initializer value has an integer suffix, validate it matches the annotation
              const litMatch = matchIntSuffix(ivalue);
              if (litMatch) {
                const suf = litMatch.suf;
                const sufKind = suf[0].toUpperCase();
                const sufBits = suf.slice(1);
                const kind = itypeToken[0].toUpperCase();
                const bits = itypeToken.slice(1);
                if (sufKind !== kind || sufBits !== bits) throw new Error('Type annotation and literal suffix mismatch');
                ivalue = litMatch.num;
              }
              decls.push(`#include <stdint.h>`); // ensure include will be present
              decls.push(`for(${cType} ${iname} = ${ivalue}; ${cond}; ${post})${bodyRaw}`);
            } else if (/^bool$/i.test(itypeToken)) {
              includes.add('stdbool');
              decls.push(`for(bool ${iname} = ${ivalue}; ${cond}; ${post})${bodyRaw}`);
            } else {
              // fallback
              decls.push(`for(${iname} ${iname} = ${ivalue}; ${cond}; ${post})${bodyRaw}`);
            }
            continue;
          }
          // No annotation: default integer type int32_t
          // strip integer suffix if present
          const intLit = matchIntSuffix(ivalue);
          if (intLit) ivalue = intLit.num;
          includes.add('stdint');
          decls.push(`for(int32_t ${iname} = ${ivalue}; ${cond}; ${post})${bodyRaw}`);
          continue;
        }
        // otherwise passthrough for as-is
        // Validate that the condition is boolean-like when present (e.g. not a bare number)
        if (cond.length > 0) {
          // replace `.length` accesses before validation
          const condNorm = replaceLengthAccess(cond);
          // A simplistic check: allow boolean literal, known bool var, comparison, or logical operators
          const condOk = (() => {
            if (isBoolLiteral(condNorm)) return true;
            if (/^[a-zA-Z_$][a-zA-Z0-9_$]*$/.test(condNorm) && vars.has(condNorm) && vars.get(condNorm)!.kind === 'bool') return true;
            if (/^(.+?)\s*(==|!=|<=|>=|<|>)\s*(.+)$/.test(condNorm)) return true;
            if (/\|\||&&/.test(condNorm)) return condNorm.split(/\|\||&&/).every((s) => detectRhsKind(s).kind === 'bool');
            return false;
          })();
          if (!condOk) throw new Error('For loop condition must be boolean');
        }
        decls.push(letDecl.replace(/;$/, ''));
        continue;
      }

      // Support optional `mut`: `let` or `let mut`
      // Allow a broader set of annotation tokens (e.g. USize)
      const match = /^let(\s+mut)?\s+([a-zA-Z_$][a-zA-Z0-9_$]*)\s*(?::\s*([A-Za-z][A-Za-z0-9_]*)\s*)?=\s*(.+);$/.exec(
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
        // USize -> size_t and allow .length on strings
        if (lower === 'usize') {
          // match x.length
          const lenMatch = /^([a-zA-Z_$][a-zA-Z0-9_$]*)\.length$/.exec(value);
          if (!lenMatch) throw new Error('USize annotation requires .length expression on a string variable');
          const ident = lenMatch[1];
          includes.add('string');
          vars.set(name, { mutable: isMut, kind: 'usize' });
          decls.push(`size_t ${name} = strlen(${ident});`);
          continue;
        }
        if (isBoolLiteral(value)) throw new Error('Type annotation and boolean literal mismatch');

        // Float annotations
        if (lower.startsWith('f')) {
          // reject integer-suffixed literals for float annotation
          if (matchIntSuffix(value)) throw new Error('Type annotation float and integer literal suffix mismatch');
          // reject char literal for float annotation
          if (/^'.'$/.test(value)) throw new Error('Type annotation float cannot be initialized with char literal');
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
        // explicit comparison expression (left op right) -> validate numeric operands and emit/throw
        const compMatch = value.match(/^(.+?)\s*(==|!=|<=|>=|<|>)\s*(.+)$/);
        if (compMatch) {
          const left = compMatch[1].trim();
          const op = compMatch[2];
          const right = compMatch[3].trim();
          const lk = detectRhsKind(left).kind;
          const rk = detectRhsKind(right).kind;
          const numeric = (k: string) => k === 'int' || k === 'uint' || k === 'float' || k === 'double';
          if (numeric(lk) && numeric(rk)) {
            includes.add('stdbool');
            const norm = normalizeNumericSuffixes(value);
            vars.set(name, { mutable: isMut, kind: 'bool' });
            decls.push(`bool ${name} = ${norm};`);
            continue;
          }
          // string comparison
          if (lk === 'string' && rk === 'string') {
            const expr = stringCompare(left, right, op);
            decls.push(`bool ${name} = ${expr};`);
            vars.set(name, { mutable: isMut, kind: 'bool' });
            continue;
          }
          throw new Error('Comparison requires numeric operands');
        }

        // detect boolean RHS (logicals or explicit bools)
        const rhsKind = detectRhsKind(value).kind;
        if (rhsKind === 'bool') {
          includes.add('stdbool');
          const norm = normalizeNumericSuffixes(value);
          vars.set(name, { mutable: isMut, kind: 'bool' });
          decls.push(`bool ${name} = ${norm};`);
          continue;
        }
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
        // string literal
        if (/^".*"$/.test(value)) {
          const decl = isMut ? `char* ${name} = ${value};` : `const char* ${name} = ${value};`;
          vars.set(name, { mutable: isMut, kind: 'string' });
          decls.push(decl);
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
        const cType = mapIntTokenToC(kind + bits);
        emitStdInt(cType, cType.startsWith('uint') ? 'uint' : 'int', bits, isMut, name, lit.num);
        continue;
      }

      // Default: int32_t
      // If value is an identifier that isn't known in current scope, reject (no leakage from brace-local vars)
      if (/^[a-zA-Z_$][a-zA-Z0-9_$]*$/.test(value) && !vars.has(value)) throw new Error(`Use of undeclared variable ${value}`);
      // Reject unsupported/unknown RHS expressions (e.g. mixed logical/numeric without explicit boolean semantics)
      const rhsInfoFinal = detectRhsKind(value);
      if (rhsInfoFinal.kind === 'unknown') throw new Error('Unsupported RHS expression');
      emitStdInt('int32_t', 'int', '32', isMut, name, value);
    }
  }

  processParts(parts);

  // Build includes header lines (consistent order)
  const includeLines: string[] = [];
  if (includes.has('stdint')) includeLines.push('#include <stdint.h>');
  if (includes.has('stdbool')) includeLines.push('#include <stdbool.h>');
  if (includes.has('string')) includeLines.push('#include <string.h>');

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
