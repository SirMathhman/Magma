export function compile(input: string): string {
  if (input.trim() === "") {
    return "";
  }
  // Manual parsing instead of regex
  const trimmed = input.trim();
  if (trimmed.startsWith("fn ") && trimmed.endsWith("{}")) {
    const fnPart = trimmed.slice(3, trimmed.length - 2).trim();
    // Expect: name() : Void =>
    const parts = fnPart.split(":");
    if (parts.length === 2) {
      const left = parts[0].trim();
      const right = parts[1].trim();
      if (right === "Void =>") {
        const nameMatch = left.match(/^(\w+)\(\)$/);
        if (nameMatch) {
          const name = nameMatch[1];
          return `void ${name}(){}`;
        }
      }
    }
  }
  throw new Error("Unsupported syntax");
}
