// Function that throws if input is non-empty, returns empty string otherwise
export function compile(input: string): string {
  if (input === "") {
    return "";
  }
  throw new Error('Input was not empty');
}
