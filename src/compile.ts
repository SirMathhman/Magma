/**
 * Accept a string and produce a string if the input is empty.
 * If `s` is an empty string, return a non-empty string result.
 * Otherwise throw an Error. Throws TypeError if input is not a string.
 */
// Helper parser state type
type ParserState = {
  src: string;
  i: number;
  len: number;
};

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

export function compile(s: string): string {
  if (typeof s !== 'string') {
    throw new TypeError('Input must be a string');
  }
  const trimmed = s.trim();
  if (trimmed === '') return 'empty';

  const state: ParserState = { src: trimmed, i: 0, len: trimmed.length };
  skipWhitespace(state);
  const funcKw = 'function';
  if (state.src.slice(state.i, state.i + funcKw.length) !== funcKw) {
    throw new Error('Input must be empty or a supported function declaration');
  }
  state.i += funcKw.length;
  skipWhitespace(state);

  const name = parseIdentifier(state);
  if (!name) throw new Error('Input must be empty or a supported function declaration');
  skipWhitespace(state);

  const paramsRaw = parseParams(state);
  if (paramsRaw === null) throw new Error('Input must be empty or a supported function declaration');
  skipWhitespace(state);

  const rt = parseReturnType(state) || 'void';
  skipWhitespace(state);

  if (!parseEmptyBody(state)) throw new Error('Input must be empty or a supported function declaration');
  skipWhitespace(state);

  if (state.i !== state.len) throw new Error('Input must be empty or a supported function declaration');

  let cParams = '';
  if (paramsRaw.length > 0) {
    cParams = paramsRaw
      .split(',')
      .map(p => p.trim().split(':')[0].trim())
      .join(', ');
  }
  return `${rt} ${name}(${cParams}){}`;
}
