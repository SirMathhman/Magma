export default function alwaysThrows(input: string): string {
  if (input === '') return '';
  throw new Error('This function always throws');
}
