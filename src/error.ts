/**
 * transformIfEmpty
 * - If `input` is an empty string, return a starter C program as a string.
 * - If `input` is non-empty, throw an Error.
 */
export function transformIfEmpty(input: string): string {
  if (input === '') {
    // A simple, valid C program placeholder — useful as a tiny compiler output.
    return '/* generated C */\nint main(void) { return 0; }\n';
  }

  throw new Error('Input must be empty');
}
