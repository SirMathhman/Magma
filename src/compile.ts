/**
 * Accept a string and produce a string if the input is empty.
 * If `s` is an empty string, return a non-empty string result.
 * Otherwise throw an Error. Throws TypeError if input is not a string.
 */
// Helper parser state interface
interface ParserState {
  src: string;
  i: number;
  len: number;
}

function skipWhitespace(state: ParserState) {
  while (state.i < state.len && /\s/.test(state.src[state.i])) state.i++;
}

function parseIdentifier(state: ParserState): string | null {
  const { src, len } = state;
  const isIdentStart = (ch: string) => /[A-Za-z_]/.test(ch);
  const isIdentPart = (ch: string) => /[A-Za-z0-9_]/.test(ch);
  if (state.i >= len || !isIdentStart(src[state.i])) return null;
  let name = src[state.i++];
  while (state.i < len && isIdentPart(src[state.i])) name += src[state.i++];
  return name;
}

function parseParams(state: ParserState): string | null {
  const { src, len } = state;
  if (state.i >= len || src[state.i] !== '(') return null;
  state.i++; // skip '('
  const start = state.i;
  while (state.i < len && src[state.i] !== ')') state.i++;
  if (state.i >= len || src[state.i] !== ')') return null;
  const raw = src.slice(start, state.i).trim();
  state.i++; // skip ')'
  return raw;
}

function parseReturnType(state: ParserState): string | null {
  const { src, len } = state;
  if (state.i < len && src[state.i] === ':') {
    state.i++; // skip ':'
    skipWhitespace(state);
    const start = state.i;
    const name = parseIdentifier(state);
    if (name) return src.slice(start, state.i).trim();
    return null;
  }
  return null;
}

function parseEmptyBody(state: ParserState): boolean {
  const { src, len } = state;
  if (state.i < len && src[state.i] === '{') {
    state.i++; // skip '{'
    skipWhitespace(state);
    if (state.i < len && src[state.i] === '}') {
      state.i++; // skip '}'
      return true;
    }
  }
  return false;
}

function parseInterfaceBody(state: ParserState): string | null {
  const { src, len } = state;
  if (state.i >= len || src[state.i] !== '{') return null;
  state.i++; // skip '{'
  const start = state.i;
  while (state.i < len && src[state.i] !== '}') state.i++;
  if (state.i >= len || src[state.i] !== '}') return null;
  const raw = src.slice(start, state.i).trim();
  state.i++; // skip '}'
  return raw;
}

function tryParseInterface(state: ParserState): string | null {
  const interfaceKw = 'interface';
  if (state.src.slice(state.i, state.i + interfaceKw.length) !== interfaceKw) return null;
  state.i += interfaceKw.length;
  skipWhitespace(state);
  const name = parseIdentifier(state);
  if (!name) throw new Error('Input must be empty or a supported function declaration');
  skipWhitespace(state);
  const bodyRaw = parseInterfaceBody(state);
  if (bodyRaw === null) throw new Error('Input must be empty or a supported function declaration');
  skipWhitespace(state);
  if (state.i !== state.len) throw new Error('Input must be empty or a supported function declaration');

  if (bodyRaw === '') {
    return `struct ${name} {};`;
  }

  const { members, needStdint } = parseMembers(bodyRaw);
  const body = members.join(' ');
  const prefix = needStdint ? '#include <stdint.h>\r\n' : '';
  return `${prefix}struct ${name} {${body}};`;
}

function parseMembers(bodyRaw: string): { members: string[]; needStdint: boolean } {
  // parse members like: "value : number" or "value : string" (allow multiple separated by ',' or ';')
  const parts = bodyRaw.split(/[;,]/).map(p => p.trim()).filter(Boolean);
  const members: string[] = [];
  let needStdint = false;
  for (const p of parts) {
    const m = /^([A-Za-z_][A-Za-z0-9_]*)\s*:\s*([A-Za-z_][A-Za-z0-9_]*)$/.exec(p);
    if (!m) throw new Error('Unsupported interface member');
    const [, propName, propType] = m;
    const { cType, needStdint: needStd } = mapType(propType);
    // accept any mapped type (including passthrough for custom types)
    members.push(`${cType} ${propName};`);
    if (needStd) needStdint = true;
  }
  return { members, needStdint };
}

function tryParseFunction(state: ParserState): string | null {
  const funcKw = 'function';
  if (state.src.slice(state.i, state.i + funcKw.length) !== funcKw) return null;
  state.i += funcKw.length;
  skipWhitespace(state);

  const name = parseIdentifier(state);
  if (!name) throw new Error('Input must be empty or a supported function declaration');
  skipWhitespace(state);

  const paramsRaw = parseParams(state);
  if (paramsRaw === null) throw new Error('Input must be empty or a supported function declaration');
  skipWhitespace(state);
  const { cParams, needStdint: needStdintParams } = parseFunctionParams(paramsRaw);
  skipWhitespace(state);

  const rt = parseReturnType(state) || 'void';
  // map return type
  const { cType: cRt, needStdint: needStdintReturn } = mapType(rt);
  skipWhitespace(state);

  if (!parseEmptyBody(state)) throw new Error('Input must be empty or a supported function declaration');
  skipWhitespace(state);

  if (state.i !== state.len) throw new Error('Input must be empty or a supported function declaration');

  const needStdint = needStdintParams || needStdintReturn;
  const prefix = needStdint ? '#include <stdint.h>\r\n' : '';
  return `${prefix}${cRt} ${name}(${cParams}){}`;
}

function mapType(t: string): { cType: string; needStdint: boolean } {
  if (t === 'number') return { cType: 'int64_t', needStdint: true };
  if (t === 'string') return { cType: 'char*', needStdint: false };
  // default (including 'void' and unknown) pass through as-is without stdint
  return { cType: t, needStdint: false };
}

function parseFunctionParams(paramsRaw: string): { cParams: string; needStdint: boolean } {
  if (paramsRaw.trim() === '') return { cParams: '', needStdint: false };
  const parts = paramsRaw.split(',').map(p => p.trim()).filter(Boolean);
  const mapped: string[] = [];
  let needStdint = false;
  for (const p of parts) {
    // match "name : type" or just "name"
    const m = /^([A-Za-z_][A-Za-z0-9_]*)(?:\s*:\s*([A-Za-z_][A-Za-z0-9_]*))?$/.exec(p);
    if (!m) throw new Error('Unsupported function parameter');
    const [, paramName, paramType] = m;
    if (!paramType) {
      // no type annotation: just name
      mapped.push(paramName);
      continue;
    }
    const { cType, needStdint: needStd } = mapType(paramType);
    mapped.push(`${cType} ${paramName}`);
    if (needStd) needStdint = true;
  }
  return { cParams: mapped.join(', '), needStdint };
}

export function compile(s: string): string {
  if (typeof s !== 'string') {
    throw new TypeError('Input must be a string');
  }
  const trimmed = s.trim();
  if (trimmed === '') return 'empty';

  const state: ParserState = { src: trimmed, i: 0, len: trimmed.length };
  skipWhitespace(state);
  const iface = tryParseInterface(state);
  if (iface) return iface;
  const fn = tryParseFunction(state);
  if (fn) return fn;

  throw new Error('Input must be empty or a supported function declaration');
}
