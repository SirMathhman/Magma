interface Option<T> {
  readonly kind: "Some" | "None";
  readonly value?: T;
}

function Some<T>(value: T): Option<T> {
  return { kind: "Some", value };
}

function None<T>(): Option<T> {
  return { kind: "None" };
}

function parseBody(trimmed: string, arrowIdx: number): Option<string> {
  const bodyStart = trimmed.indexOf("{", arrowIdx);
  const bodyEnd = trimmed.lastIndexOf("}");
  if (bodyStart === -1 || bodyEnd === -1 || bodyEnd <= bodyStart) return None();
  return Some(trimmed.slice(bodyStart + 1, bodyEnd).trim());
}

function parseSignature(signature: string): Option<{ name: string; params: string; type: string }> {
  const openParenIdx = signature.indexOf("(");
  const closeParenIdx = signature.indexOf(")");
  const colonIdx = signature.indexOf(":", closeParenIdx);
  if (openParenIdx === -1 || closeParenIdx === -1 || colonIdx === -1) return None();
  const name = signature.slice(0, openParenIdx).trim();
  const params = signature.slice(openParenIdx + 1, closeParenIdx).trim();
  const type = signature.slice(colonIdx + 1).trim();
  return Some({ name, params, type });
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

function parseFunction(input: string): Option<ParsedFunction> {
  const trimmed = input.trim();
  if (!trimmed.startsWith("fn ") || !trimmed.includes("=>")) return None();
  const signatureStart = 3;
  const arrowIdx = trimmed.indexOf("=>");
  const signature = trimmed.slice(signatureStart, arrowIdx).trim();
  const body = parseBody(trimmed, arrowIdx);
  if (body.kind === "None") return None();
  const sig = parseSignature(signature);
  if (sig.kind === "None") return None();
  let paramList: { name: string; type: string }[] = [];
  if (sig.value!.params.length > 0) {
    try {
      paramList = parseParams(sig.value!.params);
    } catch {
      return None();
    }
  }
  return Some({
    name: sig.value!.name,
    type: sig.value!.type,
    body: body.value!,
    params: paramList,
  });
}

function toCFunction(parsed: ParsedFunction): Option<string> {
  // Support params
  const paramStr = parsed.params && parsed.params.length > 0
    ? parsed.params.map((p: { name: string; type: string }) => {
      if (p.type === "I32") return `int ${p.name}`;
      // Add more types as needed
      return `${p.type} ${p.name}`;
    }).join(", ")
    : "";
  if (parsed.type === "Void") {
    return Some(`void ${parsed.name}(${paramStr}){${parsed.body}}`);
  }
  if (parsed.type === "I32") {
    return Some(`int ${parsed.name}(${paramStr}){${parsed.body}}`);
  }
  return None();
}

export function compile(input: string): string {
  if (input.trim() === "") {
    return "";
  }
  const parsed = parseFunction(input);
  if (parsed.kind === "Some") {
    const cCode = toCFunction(parsed.value!);
    if (cCode.kind === "Some") return cCode.value!;
  }
  throw new Error("Unsupported syntax");
}
