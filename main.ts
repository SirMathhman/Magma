const fs = require('fs');
const path = require('path');
const { compile } = require('./compile');

function main() {
  const inputPath = path.resolve(__dirname, 'index.mgs');
  const outputPath = path.resolve(__dirname, 'index.c');
  if (!fs.existsSync(inputPath)) {
    console.error('Input file ./index.mgs not found.');
    process.exit(1);
  }
  const magmaSource = fs.readFileSync(inputPath, 'utf8');
  const cOutput = compile(magmaSource);
  fs.writeFileSync(outputPath, cOutput, 'utf8');
  console.log('Compiled ./index.mgs to ./index.c');
}

if (require.main === module) {
  main();
}
