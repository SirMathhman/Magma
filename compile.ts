// Function that throws if input is non-empty, returns empty string otherwise
export function compile(input: string): string {
  if (input === "") {
    return "";
  }
  // Accept 'let <name> : <type> = <value>;' and produce correct C type
  const prefix = "let ";
  const suffix = ";";
  if (input.startsWith(prefix) && input.endsWith(suffix)) {
    const body = input.slice(prefix.length, -suffix.length);
    const parts = body.split(" = ");
    if (parts.length === 2) {
      const left = parts[0];
      const value = parts[1];
      const leftParts = left.split(" : ");
      if (leftParts.length === 2) {
        const name = leftParts[0];
        const type = leftParts[1];
        let cType = "";
        if (type === "I32") {
          cType = "int32_t";
        } else if (type === "I16") {
          cType = "int16_t";
        }
        if (cType) {
          return `${cType} ${name} = ${value};`;
        }
      }
    }
  }
  throw new Error('Input was not empty');
}
