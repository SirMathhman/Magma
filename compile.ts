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

export function compile(input: string) {
  const src = input.trim();
  if (src === "") return "";

  const letPrefix = "let ";
  if (src.startsWith(letPrefix) && src.endsWith(";")) {
    const body = src.substring(letPrefix.length, src.length - 1).trim();
    const eqIndex = body.indexOf("=");
    if (eqIndex === -1) throw new Error("Invalid let declaration");

    // Check for optional type annotation between name and '='
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
    if (!isValidIdentifier(name)) throw new Error("Invalid identifier");

    // Map source types to target C types. Default to int32_t for now.
    const typeMap: { [k: string]: string } = {
      I32: "int32_t",
    };
    const outType = typeName ? (typeMap[typeName] || (() => { throw new Error("Unsupported type"); })()) : "int32_t";
    return `${outType} ${name} = ${value};`;
  }

  throw new Error("compile only supports empty input or simple let declarations");
}
