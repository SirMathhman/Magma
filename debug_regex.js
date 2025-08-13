const declPattern = /fn\s+(\w+)<([A-Za-z0-9_,\s]+)>\s*\(([^)]*)\)\s*:\s*([A-Za-z0-9_]+)\s*=>\s*\{[^{}]*(?:\{[^{}]*\}[^{}]*)?\}/g;

const input = 'fn accept<T>(array : [T; 3]) : Void => {}';
console.log('Input:', input);

const match = declPattern.exec(input);
console.log('Match:', match);

// Test with a simpler pattern
const simplePattern = /fn\s+(\w+)<([A-Za-z0-9_,\s]+)>\s*\(([^)]*)\)\s*:\s*([A-Za-z0-9_]+)\s*=>\s*\{.*?\}/g;
const match2 = simplePattern.exec(input);
console.log('Simple match:', match2);

// Test individual parts
const namePattern = /fn\s+(\w+)</;
const nameMatch = input.match(namePattern);
console.log('Name match:', nameMatch);

const typeParamPattern = /<([A-Za-z0-9_,\s]+)>/;
const typeParamMatch = input.match(typeParamPattern);
console.log('Type param match:', typeParamMatch);