export default function alwaysThrows(input: string): string {
  if (input === '') return '';

  // Transform a simple `let name = value;` JavaScript declaration
  // into a C-style `int32_t name = value;` with stdint.h header.
  // Only handle a very small subset as requested.
  const letDecl = input.trim();
  // Match: let name [ : Type ] = value; where Type is I8..I64 or U8..U64
  // Accept IU types (I8..I64, U8..U64) or Bool as annotation
  const match = /^let\s+([a-zA-Z_$][a-zA-Z0-9_$]*)\s*(?::\s*(([IU](?:8|16|32|64))|[bB]ool)\s*)?=\s*(.+);$/.exec(letDecl);
  if (match) {
    const name = match[1];
    const typeToken = match[2]; // e.g. 'I32', 'U8' or 'Bool'
    // The raw value is captured by the last capture group in the regex
    const rawValue = match[match.length - 1];
    let value = rawValue.trim();

    // Default header and type
    let header = '#include <stdint.h>';
    let cType = 'int32_t';

    // If no annotation and value is a boolean literal, return bool with stdbool.h
    if (!typeToken && (value === 'true' || value === 'false')) {
      return `#include <stdbool.h>\r\nbool ${name} = ${value};`;
    }

    // Check for integer literal suffixes like 0I32 or 123U8 (case-insensitive)
    // If annotation is missing, use suffix to determine the C type.
    if (typeToken) {
      const lower = typeToken.toLowerCase();
      if (lower === 'bool') {
        // annotated as Bool: only accept boolean literals
        if (value === 'true' || value === 'false') {
          return `#include <stdbool.h>\r\nbool ${name} = ${value};`;
        }
        throw new Error('Type annotation Bool requires boolean literal');
      }
      // If annotation is a numeric type but value is boolean, that's invalid
      if (value === 'true' || value === 'false') {
        throw new Error('Type annotation and boolean literal mismatch');
      }
      const kind = typeToken[0].toUpperCase();
      const bits = typeToken.slice(1);
      if (kind === 'I') cType = `int${bits}_t`;
      else cType = `uint${bits}_t`;

      // If annotation present, check for a literal suffix. If present and mismatched, throw.
      const litMatch = value.match(/^(?<num>[0-9]+)(?<suf>[iIuU](?:8|16|32|64))$/);
      if (litMatch && litMatch.groups) {
        const suf = litMatch.groups['suf'];
        const sufKind = suf[0].toUpperCase();
        const sufBits = suf.slice(1);
        if (sufKind !== kind || sufBits !== bits) {
          throw new Error('Type annotation and literal suffix mismatch');
        }
        value = litMatch.groups['num'];
      } else {
        // No literal suffix, just strip any accidental suffix pattern
        value = value.replace(/([0-9]+)(?:[iIuU](?:8|16|32|64))$/, '$1');
      }
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

    // Choose header based on chosen type
    if (cType === 'bool') header = '#include <stdbool.h>';
    else header = '#include <stdint.h>';

    return `${header}\r\n${cType} ${name} = ${value};`;
  }

  throw new Error('This function always throws');
}
