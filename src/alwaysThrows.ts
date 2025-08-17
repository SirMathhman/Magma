export default function alwaysThrows(input: string): string {
  if (input === '') return '';

  // Transform a simple `let name = value;` JavaScript declaration
  // into a C-style `int32_t name = value;` with stdint.h header.
  // Only handle a very small subset as requested.
  const letDecl = input.trim();
  const match = /^let\s+([a-zA-Z_$][a-zA-Z0-9_$]*)\s*=\s*(.+);$/.exec(letDecl);
  if (match) {
    const name = match[1];
    const value = match[2];
    return `#include <stdint.h>\r\nint32_t ${name} = ${value};`;
  }

  throw new Error('This function always throws');
}
