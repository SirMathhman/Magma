function compile(input) {
  if (typeof input === 'string' && input.length === 0) {
    return "Input was empty.";
  }
  // Match let x : TYPE = value; for U8-U64 and I8-I64
  const typeMap = {
    'U8': 'uint8_t',
    'U16': 'uint16_t',
    'U32': 'uint32_t',
    'U64': 'uint64_t',
    'I8': 'int8_t',
    'I16': 'int16_t',
    'I32': 'int32_t',
    'I64': 'int64_t'
  };
  const magmaRegex = /^let\s+(\w+)\s*:\s*(U8|U16|U32|U64|I8|I16|I32|I64)\s*=\s*(.+);$/;
  const magmaMatch = input.match(magmaRegex);
  if (magmaMatch) {
    const varName = magmaMatch[1];
    const type = magmaMatch[2];
    const value = magmaMatch[3];
    return `${typeMap[type]} ${varName} = ${value};`;
  }

  // Match let x = 0TYPE;
  const literalRegex = /^let\s+(\w+)\s*=\s*(\d+)(U8|U16|U32|U64|I8|I16|I32|I64);$/;
  const literalMatch = input.match(literalRegex);
  if (literalMatch) {
    const varName = literalMatch[1];
    const value = literalMatch[2];
    const type = literalMatch[3];
    return `${typeMap[type]} ${varName} = ${value};`;
  }

  // Match let x = 0;
  const jsRegex = /^let\s+(\w+)\s*=\s*(.+);$/;
  const jsMatch = input.match(jsRegex);
  if (jsMatch) {
    const varName = jsMatch[1];
    const value = jsMatch[2];
    return `int32_t ${varName} = ${value};`;
  }
  throw new Error("Unsupported input format.");
}

module.exports = compile;
