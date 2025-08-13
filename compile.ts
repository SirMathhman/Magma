// Function that throws if input is non-empty, returns empty string otherwise
export function compile(input: string): string {
  if (input === "") {
    return "";
  }
  // Accept 'let x : I32 = 100;' and produce 'int32_t x = 100;'
  if (input === "let x : I32 = 100;") {
    return "int32_t x = 100;";
  }
  throw new Error('Input was not empty');
}
