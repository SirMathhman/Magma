import fs from 'fs';
import path from 'path';
import compile from './compile';

const srcPath = path.resolve(process.cwd(), 'index.mgs');
const outPath = path.resolve(process.cwd(), 'index.c');

if (!fs.existsSync(srcPath)) {
  // nothing to do
  // eslint-disable-next-line no-console
  console.log('index.mgs not found, nothing to compile');
  process.exit(0);
}

const src = fs.readFileSync(srcPath, { encoding: 'utf8' });
const out = compile(src);
fs.writeFileSync(outPath, out, { encoding: 'utf8' });
// eslint-disable-next-line no-console
console.log(`Wrote ${outPath}`);

