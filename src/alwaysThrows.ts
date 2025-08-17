export default function alwaysThrows(input: string): string {
  if (input === '') return '';

  // Transform a simple `let name = value;` JavaScript declaration
  // into a C-style `int32_t name = value;` with stdint.h header.
  // Only handle a very small subset as requested.
  const letDecl = input.trim();
  // Match: let name [ : I32 ] = value;
  const match = /^let\s+([a-zA-Z_$][a-zA-Z0-9_$]*)\s*(?::\s*I32\s*)?=\s*(.+);$/.exec(letDecl);
  if (match) {
    const name = match[1];
    const rawValue = match[2];
    let value = rawValue.trim();
    // Strip a trailing I32 literal suffix (e.g. 0I32 -> 0)
    value = value.replace(/([0-9]+)I32$/, '$1');
    return `#include <stdint.h>\r\nint32_t ${name} = ${value};`;
  }

  throw new Error('This function always throws');
}
