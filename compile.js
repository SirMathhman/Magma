function compile(input) {
  if (typeof input === 'string' && input.length === 0) {
    return "Input was empty.";
  }
  // Match let x : I32 = 0;
  const magmaRegex = /^let\s+(\w+)\s*:\s*I32\s*=\s*(.+);$/;
  const magmaMatch = input.match(magmaRegex);
  if (magmaMatch) {
    const varName = magmaMatch[1];
    const value = magmaMatch[2];
    return `int32_t ${varName} = ${value};`;
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
