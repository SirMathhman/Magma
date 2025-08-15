/**
 * Very small Magma -> C compiler for a single subset of the language.
 * Currently supports statements like: `let x : I32 = 0;`
 */
export function compileMagmaToC(src: string): string {
  // Split by semicolon and parse simple let statements
  const stmts = src
    .split(';')
    .map(s => s.trim())
    .filter(Boolean);

  const bodyLines: string[] = [];

  for (const stmt of stmts) {
    // Match: let <ident> : I32 = <integer>
    const m = stmt.match(/^let\s+([A-Za-z_]\w*)\s*:\s*(I32)\s*=\s*(-?\d+)$/);
    if (!m) {
      throw new Error(`Unsupported or invalid statement: ${stmt}`);
    }

    const [, name, ty, value] = m;

    let ctype: string;
    if (ty === 'I32') ctype = 'int32_t';
    else throw new Error(`Unsupported type: ${ty}`);

    bodyLines.push(`${ctype} ${name} = ${value};`);
  }

  const out = `#include <stdint.h>\n\nint main(void) {\n  ${bodyLines.join('\n  ')}\n  return 0;\n}\n`;
  return out;
}

export default compileMagmaToC;
