// Function that throws if input is non-empty, returns empty string otherwise
export function compile(input: string): string {
  if (input === "") {
    return "";
  }
  // Accept 'let <name> : <type> = <value>;' and produce correct C type
  const prefix = "let ";
  const suffix = ";";
  // Handle function syntax: fn <name>() : <type> => {}
  const fnMatch = input.match(/^fn\s+(\w+)\s*\(\)\s*:\s*(\w+)\s*=>\s*\{\s*\}$/);
  if (fnMatch) {
    const fnName = fnMatch[1];
    const fnType = fnMatch[2];
    let cType = '';
    if (fnType === 'Void') {
      cType = 'void';
    }
    if (!cType) throw new Error('Input was not empty');
    return `${cType} ${fnName}(){}`;
  }
  // Handle multiple statements separated by ';'
  const statements = input.split(';').map(s => s.trim()).filter(Boolean);
  if (statements.length > 1) {
    // Type inference map
    const inferredTypes: Record<string, string> = {};
    let output = [];
    for (const stmt of statements) {
      if (!stmt.startsWith(prefix)) throw new Error('Input was not empty');
      const body = stmt.slice(prefix.length);
      const parts = body.split(' = ');
      if (parts.length !== 2) throw new Error('Input was not empty');
      const left = parts[0];
      const value = parts[1];
      let name = left;
      let type = '';
      if (left.includes(' : ')) {
        const leftParts = left.split(' : ');
        name = leftParts[0];
        type = leftParts[1];
      }
      // Type inference
      if (!type) {
        // Infer type from value or previous assignment
        if (/^\d+$/.test(value)) {
          type = 'I32';
        } else if (inferredTypes[value]) {
          type = inferredTypes[value];
        } else {
          throw new Error('Input was not empty');
        }
      }
      inferredTypes[name] = type;
      let cType = '';
      if (type === 'I8') {
        cType = 'int8_t';
      } else if (type === 'I16') {
        cType = 'int16_t';
      } else if (type === 'I32') {
        cType = 'int32_t';
      } else if (type === 'I64') {
        cType = 'int64_t';
      } else if (type === 'U8') {
        cType = 'uint8_t';
      } else if (type === 'U16') {
        cType = 'uint16_t';
      } else if (type === 'U32') {
        cType = 'uint32_t';
      } else if (type === 'U64') {
        cType = 'uint64_t';
      } else if (type === 'Bool' && (value === 'true' || value === 'false')) {
        cType = 'bool';
      }
      if (!cType) throw new Error('Input was not empty');
      output.push(`${cType} ${name} = ${value};`);
    }
    return output.join(' ');
  } else if (input.startsWith(prefix) && input.endsWith(suffix)) {
    const body = input.slice(prefix.length, -suffix.length);
    const parts = body.split(" = ");
    if (parts.length === 2) {
      const left = parts[0];
      const value = parts[1];
      const leftParts = left.split(" : ");
      if (leftParts.length === 2) {
        const name = leftParts[0];
        const type = leftParts[1];
        let cType = "";
        if (type === "I8") {
          cType = "int8_t";
        } else if (type === "I16") {
          cType = "int16_t";
        } else if (type === "I32") {
          cType = "int32_t";
        } else if (type === "I64") {
          cType = "int64_t";
        } else if (type === "U8") {
          cType = "uint8_t";
        } else if (type === "U16") {
          cType = "uint16_t";
        } else if (type === "U32") {
          cType = "uint32_t";
        } else if (type === "U64") {
          cType = "uint64_t";
        } else if (type === "Bool" && (value === "true" || value === "false")) {
          cType = "bool";
        }
        if (cType) {
          return `${cType} ${name} = ${value};`;
        }
      }
    }
  }
  throw new Error('Input was not empty');
}
