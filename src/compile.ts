/**
 * Accept a string and produce a string if the input is empty.
 * If `s` is an empty string, return a non-empty string result.
 * Otherwise throw an Error. Throws TypeError if input is not a string.
 */
export function compile(s: string): string {
  if (typeof s !== 'string') {
    throw new TypeError('Input must be a string');
  }
  // Manual parser for a minimal TypeScript function -> C function compilation
  // Supported shape: function <name>(<params>) : <ReturnType> { }
  const src = s.trim();
  let i = 0;
  const len = src.length;

  function skipWhitespace() {
    while (i < len && /\s/.test(src[i])) i++;
  }

  // expect the literal 'function'
  skipWhitespace();
  const funcKw = 'function';
  if (src.slice(i, i + funcKw.length) === funcKw) {
    i += funcKw.length;
    skipWhitespace();

    // parse identifier
    if (i >= len) {
      // fallthrough to other checks
    } else {
      const isIdentStart = (ch: string) => /[A-Za-z_]/.test(ch);
      const isIdentPart = (ch: string) => /[A-Za-z0-9_]/.test(ch);
      if (!isIdentStart(src[i])) {
        // not a valid identifier start
      } else {
        let name = src[i++];
        while (i < len && isIdentPart(src[i])) name += src[i++];
        skipWhitespace();

        // parse params
        if (i < len && src[i] === '(') {
          i++; // skip '('
          const paramsStart = i;
          // find matching ')'
          while (i < len && src[i] !== ')') i++;
          if (i < len && src[i] === ')') {
            const paramsRaw = src.slice(paramsStart, i).trim();
            i++; // skip ')'
            skipWhitespace();

            // optional return type
            let returnType = 'void';
            if (i < len && src[i] === ':') {
              i++; // skip ':'
              skipWhitespace();
              // parse return type identifier
              const rtStart = i;
              if (i < len && isIdentStart(src[i])) {
                i++;
                while (i < len && isIdentPart(src[i])) i++;
                returnType = src.slice(rtStart, i).trim();
              }
              skipWhitespace();
            }

            // expect empty body { }
            if (i < len && src[i] === '{') {
              i++; // skip '{'
              skipWhitespace();
              if (i < len && src[i] === '}') {
                i++; // skip '}'
                skipWhitespace();
                if (i === len) {
                  // build C params by stripping types from paramsRaw
                  let cParams = '';
                  if (paramsRaw.length > 0) {
                    cParams = paramsRaw
                      .split(',')
                      .map(p => p.trim().split(':')[0].trim())
                      .join(', ');
                  }
                  return `${returnType} ${name}(${cParams}){}`;
                }
              }
            }
          }
        }
      }
    }
  }

  if (s === '') {
    return 'empty';
  }

  throw new Error('Input must be empty or a supported function declaration');
}
