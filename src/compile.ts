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
  const bodyStart = trimmed.indexOf("{", arrowIdx);
  const bodyEnd = trimmed.lastIndexOf("}");
  if (bodyStart === -1 || bodyEnd === -1 || bodyEnd <= bodyStart) return null;
  const body = trimmed.slice(bodyStart + 1, bodyEnd).trim();
  // Match function name and parameters
  const fnMatch = signature.match(/^(\w+)\(([^)]*)\)\s*:\s*(\w+)$/);
  if (!fnMatch) return null;
  const name = fnMatch[1];
  const params = fnMatch[2].trim();
  const type = fnMatch[3];
  // Parse parameters (support single param for now)
  let paramList: { name: string; type: string }[] = [];
  if (params.length > 0) {
    // Only support one param for now
    const paramMatch = params.match(/^(\w+)\s*:\s*(\w+)$/);
    if (!paramMatch) return null;
    paramList.push({ name: paramMatch[1], type: paramMatch[2] });
  }
  return {
    name,
    type,
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
