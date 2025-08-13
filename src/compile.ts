function parseFunction(input: string): {
  name: string;
  type: string;
  body: string;
} | null {
  const trimmed = input.trim();
  if (!trimmed.startsWith("fn ") || !trimmed.includes("=>")) return null;
  const signatureStart = 3;
  const arrowIdx = trimmed.indexOf("=>");
  const signature = trimmed.slice(signatureStart, arrowIdx).trim();
  const bodyStart = trimmed.indexOf("{", arrowIdx);
  const bodyEnd = trimmed.lastIndexOf("}");
  if (bodyStart === -1 || bodyEnd === -1 || bodyEnd <= bodyStart) return null;
  const body = trimmed.slice(bodyStart + 1, bodyEnd).trim();
  const sigParts = signature.split(":");
  if (sigParts.length !== 2) return null;
  const left = sigParts[0].trim();
  const right = sigParts[1].trim();
  const nameMatch = left.match(/^(\w+)\(\)$/);
  if (!nameMatch) return null;
  return {
    name: nameMatch[1],
    type: right,
    body,
  };
}

function toCFunction(parsed: { name: string; type: string; body: string }): string | null {
  if (parsed.type === "Void") {
    return `void ${parsed.name}(){${parsed.body}}`;
  }
  if (parsed.type === "I32") {
    return `int ${parsed.name}(){${parsed.body}}`;
  }
  return null;
}

export function compile(input: string): string {
  if (input.trim() === "") {
    return "";
  }
  const parsed = parseFunction(input);
  if (parsed) {
    const cCode = toCFunction(parsed);
    if (cCode) return cCode;
  }
  throw new Error("Unsupported syntax");
}
