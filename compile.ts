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

  // Detect a pattern: optional leading +/-, digits, then optional suffix.
  let i = 0;
  if ((value[0] === '+' || value[0] === '-') && value.length > 1) i = 1;

  // Must have at least one digit
  const digitsStart = i;
  while (i < value.length && isDigit(value[i])) i++;
  const digitsEnd = i;
  if (digitsEnd === digitsStart) {
    // Not an integer literal at the start; leave value unchanged.
    return value;
  }

  if (digitsEnd === value.length) {
    // Pure integer literal, append the type suffix.
    return `${value}${typeName}`;
  }

  // There is a trailing suffix after the digits. Validate it matches the declared type.
  const suffix = value.substring(digitsEnd);
  // Suffix must exactly equal the type name to be valid.
  if (suffix === typeName) {
    return value;
  }

  // Any other suffix is a mismatch for the declared type.
  throw new Error("Literal type suffix does not match declared type");
}

const supportedTypes = [
  "U8",
  "U16",
  "U32",
  "U64",
  "I8",
  "I16",
  "I32",
  "I64",
  "F32",
  "F64",
];

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
      // Only allow the supported type names.
      if (supportedTypes.indexOf(typeName) === -1) throw new Error("Unsupported type");

      // For floating types, emit C-style declarations directly.
      if (typeName === "F32" || typeName === "F64") {
        const floatMap: { [k: string]: string } = { F32: "float", F64: "double" };
        return `${floatMap[typeName]} ${name} = ${value};`;
      }

      const outValue = formatTypedValue(value, typeName);
      return `let ${name} : ${typeName} = ${outValue};`;
    }

    // Map source types to target C types. Default to int32_t for now.
    const typeMap: { [k: string]: string } = {
      I8: "int8_t",
      I16: "int16_t",
      I32: "int32_t",
      I64: "int64_t",
      U8: "uint8_t",
      U16: "uint16_t",
      U32: "uint32_t",
      U64: "uint64_t",
    };
    const outType = typeMap["I32"] || "int32_t";
    return `${outType} ${name} = ${value};`;
  }

  throw new Error("compile only supports empty input or simple let declarations");
}
