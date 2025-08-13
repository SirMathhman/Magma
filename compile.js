function compile(input) {
  if (typeof input === 'string' && input.length === 0) {
    return "Input was empty.";
  }
  // Match let x : I32 = 0;
  const regex = /^let\s+(\w+)\s*:\s*I32\s*=\s*(.+);$/;
  const match = input.match(regex);
  if (match) {
    const varName = match[1];
    const value = match[2];
    return `int32_t ${varName} = ${value};`;
  }
  throw new Error("Unsupported input format.");
}

module.exports = compile;
