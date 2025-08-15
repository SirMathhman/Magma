function isLetter(ch: string) {
  const code = ch.charCodeAt(0);
  return (code >= 65 && code <= 90) || (code >= 97 && code <= 122);
}

function isDigit(ch: string) {
  const code = ch.charCodeAt(0);
  return code >= 48 && code <= 57;
}

function isValidIdentifier(name: string) {
  if (!name || name.length === 0) return false;
  const first = name[0];
  if (!(isLetter(first) || first === '_')) return false;
  for (let i = 1; i < name.length; i++) {
    const ch = name[i];
    if (!(isLetter(ch) || isDigit(ch) || ch === '_')) return false;
  }
  return true;
}

function parseLetBody(body: string): { name: string; typeName: string | null; value: string } {
  const eqIndex = body.indexOf("=");
  if (eqIndex === -1) throw new Error("Invalid let declaration");

  const colonIndex = body.indexOf(":");
  let name: string;
  let typeName: string | null = null;
  if (colonIndex !== -1 && colonIndex < eqIndex) {
    name = body.substring(0, colonIndex).trim();
    typeName = body.substring(colonIndex + 1, eqIndex).trim();
  } else {
    name = body.substring(0, eqIndex).trim();
  }

  const value = body.substring(eqIndex + 1).trim();
  return { name, typeName, value };
}

function formatTypedValue(value: string, typeName: string): string {
  if (value.length === 0) return value;

  let isIntegerLiteral = true;
  for (let i = 0; i < value.length && isIntegerLiteral; i++) {
    const ch = value[i];
    if (!isDigit(ch)) {
      if ((ch === '+' || ch === '-') && i === 0) continue;
      isIntegerLiteral = false;
    }
  }

  return isIntegerLiteral ? `${value}${typeName}` : value;
}

export function compile(input: string) {
  const src = input.trim();
  if (src === "") return "";

  const letPrefix = "let ";
  if (src.startsWith(letPrefix) && src.endsWith(";")) {
    const body = src.substring(letPrefix.length, src.length - 1).trim();
    const { name, typeName, value } = parseLetBody(body);
    if (!isValidIdentifier(name)) throw new Error("Invalid identifier");

    // If a source type annotation is present, emit a source-style declaration
    // and append the type name to integer literals (e.g. `0` -> `0I32`).
    if (typeName) {
      // Only allow simple identifier type names (no regexes per instructions).
      if (!isValidIdentifier(typeName)) throw new Error("Unsupported type");

      const outValue = formatTypedValue(value, typeName);
      return `let ${name} : ${typeName} = ${outValue};`;
    }

    // Map source types to target C types. Default to int32_t for now.
    const typeMap: { [k: string]: string } = {
      I32: "int32_t",
    };
    const outType = typeMap["I32"] || "int32_t";
    return `${outType} ${name} = ${value};`;
  }

  throw new Error("compile only supports empty input or simple let declarations");
}
