export default function alwaysThrows(input: string): string {
  if (input === '') return '';

  // Transform a simple `let name = value;` JavaScript declaration
  // into a C-style `int32_t name = value;` with stdint.h header.
  // Only handle a very small subset as requested.
  const letDecl = input.trim();
  // Match: let name [ : Type ] = value; where Type is I8..I64 or U8..U64
  const match = /^let\s+([a-zA-Z_$][a-zA-Z0-9_$]*)\s*(?::\s*([IU](?:8|16|32|64))\s*)?=\s*(.+);$/.exec(letDecl);
  if (match) {
    const name = match[1];
    const typeToken = match[2]; // e.g. 'I32' or 'U8'
    const rawValue = match[3];
    let value = rawValue.trim();

    // Check for integer literal suffixes like 0I32 or 123U8 (case-insensitive)
    // If annotation is missing, use suffix to determine the C type.
    let cType = 'int32_t';
    if (typeToken) {
      const kind = typeToken[0].toUpperCase();
      const bits = typeToken.slice(1);
      if (kind === 'I') cType = `int${bits}_t`;
      else cType = `uint${bits}_t`;
      // If annotation present, just strip any matching suffix from the value
      value = value.replace(/([0-9]+)(?:[iIuU](?:8|16|32|64))$/, '$1');
    } else {
      const litMatch = value.match(/^(?<num>[0-9]+)(?<suf>[iIuU](?:8|16|32|64))$/);
      if (litMatch && litMatch.groups) {
        const suf = litMatch.groups['suf'];
        const kind = suf[0].toUpperCase();
        const bits = suf.slice(1);
        if (kind === 'I') cType = `int${bits}_t`;
        else cType = `uint${bits}_t`;
        value = litMatch.groups['num'];
      }
    }

    return `#include <stdint.h>\r\n${cType} ${name} = ${value};`;
  }

  throw new Error('This function always throws');
}
