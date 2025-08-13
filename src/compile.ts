function parseBody(trimmed: string, arrowIdx: number): string | null {
  const bodyStart = trimmed.indexOf("{", arrowIdx);
  const bodyEnd = trimmed.lastIndexOf("}");
  if (bodyStart === -1 || bodyEnd === -1 || bodyEnd <= bodyStart) return null;
  return trimmed.slice(bodyStart + 1, bodyEnd).trim();
}

function parseSignature(signature: string): { name: string; params: string; type: string } | null {
  const openParenIdx = signature.indexOf("(");
  const closeParenIdx = signature.indexOf(")");
  const colonIdx = signature.indexOf(":", closeParenIdx);
  if (openParenIdx === -1 || closeParenIdx === -1 || colonIdx === -1) return null;
  const name = signature.slice(0, openParenIdx).trim();
  const params = signature.slice(openParenIdx + 1, closeParenIdx).trim();
  const type = signature.slice(colonIdx + 1).trim();
  return { name, params, type };
}

function parseParams(params: string): { name: string; type: string }[] {
  if (params.length === 0) return [];
  return params.split(",").map(p => {
    const parts = p.split(":");
    if (parts.length !== 2) throw new Error("Invalid parameter format");
    return { name: parts[0].trim(), type: parts[1].trim() };
  });
}
interface ParsedFunction {
  name: string;
  type: string;
  body: string;
  params: { name: string; type: string }[];
}

function parseFunction(input: string): ParsedFunction | null {
  const trimmed = input.trim();
  if (!trimmed.startsWith("fn ") || !trimmed.includes("=>")) return null;
  const signatureStart = 3;
  const arrowIdx = trimmed.indexOf("=>");
  const signature = trimmed.slice(signatureStart, arrowIdx).trim();
  const body = parseBody(trimmed, arrowIdx);
  if (body === null) return null;
  const sig = parseSignature(signature);
  if (!sig) return null;
  let paramList: { name: string; type: string }[] = [];
  if (sig.params.length > 0) {
    try {
      paramList = parseParams(sig.params);
    } catch {
      return null;
    }
  }
  return {
    name: sig.name,
    type: sig.type,
    body,
    params: paramList,
  };
}

function toCFunction(parsed: ParsedFunction): string | null {
  // Support params
  const paramStr = parsed.params && parsed.params.length > 0
    ? parsed.params.map((p: { name: string; type: string }) => {
      if (p.type === "I32") return `int ${p.name}`;
      // Add more types as needed
      return `${p.type} ${p.name}`;
    }).join(", ")
    : "";
  if (parsed.type === "Void") {
    return `void ${parsed.name}(${paramStr}){${parsed.body}}`;
  }
  if (parsed.type === "I32") {
    return `int ${parsed.name}(${paramStr}){${parsed.body}}`;
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
