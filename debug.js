const { compile } = require('./compile.ts');

// Test the specific string
const input = 'fn empty(): Void;';
console.log('Input:', JSON.stringify(input));
console.log('Ends with semicolon?', input.endsWith(';'));

try {
  const result = compile(input);
  console.log('Result:', result);
} catch (error) {
  console.log('Error:', error.message);
}
