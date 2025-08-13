// Function that throws if input is non-empty, returns empty string otherwise
export function compile(input: string): string {
  if (input === "") {
    return "";
  }
  // Accept 'let <name> : I32 = <value>;' and produce 'int32_t <name> = <value>;'
  const prefix = "let ";
  const suffix = ";";
  if (input.startsWith(prefix) && input.endsWith(suffix)) {
    const body = input.slice(prefix.length, -suffix.length);
    const parts = body.split(" = ");
    if (parts.length === 2) {
      const left = parts[0];
      const value = parts[1];
      const leftParts = left.split(" : ");
      if (leftParts.length === 2 && leftParts[1] === "I32") {
        const name = leftParts[0];
        return `int32_t ${name} = ${value};`;
      }
    }
  }
  throw new Error('Input was not empty');
}
