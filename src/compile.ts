export function compile(input: string): string {
  if (input.trim() === "") {
    return "";
  }
  // Match the custom function syntax: fn name() : Void => {}
  const fnRegex = /^fn\s+(\w+)\s*\(\)\s*:\s*Void\s*=>\s*\{\s*\}$/;
  const match = input.match(fnRegex);
  if (match) {
    const name = match[1];
    return `void ${name}(){}`;
  }
  throw new Error("Unsupported syntax");
}
