/**
 * Accept a string and produce a string if the input is empty.
 * If `s` is an empty string, return a non-empty string result.
 * Otherwise throw an Error. Throws TypeError if input is not a string.
 */
export function compile(s: string): string {
  if (typeof s !== 'string') {
    throw new TypeError('Input must be a string');
  }
  // Minimal TypeScript function -> C function compilation
  // Matches: function name(params) : returnType { }
  const fnRegex = /^\s*function\s+([A-Za-z_]\w*)\s*\(\s*([^)]*)\)\s*(?::\s*([A-Za-z_]\w*))?\s*\{\s*\}\s*$/;
  const m = s.match(fnRegex);
  if (m) {
    const name = m[1];
    const params = m[2].trim();
    const returnType = m[3] ? m[3].trim() : 'void';
    // Convert parameters by stripping TypeScript types (keep names only)
    let cParams = '';
    if (params.length > 0) {
      cParams = params
        .split(',')
        .map(p => p.trim().split(':')[0].trim())
        .join(', ');
    }
    return `${returnType} ${name}(${cParams}){}`;
  }

  if (s === '') {
    return 'empty';
  }

  throw new Error('Input must be empty or a supported function declaration');
}
