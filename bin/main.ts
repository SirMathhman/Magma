import * as fs from 'fs';
import { compile } from '../compile';

const inPath = './index.mgs';
const outPath = './index.c';

function main() {
  try {
    if (!fs.existsSync(inPath)) {
      console.error(`Input file not found: ${inPath}`);
      process.exit(1);
    }
    const src = fs.readFileSync(inPath, 'utf8');
    const out = compile(src);
    fs.writeFileSync(outPath, out, 'utf8');
    console.log(`Wrote ${outPath}`);
  } catch (e: unknown) {
    const msg = e instanceof Error ? e.message : String(e);
    console.error('Compilation failed:', msg);
    process.exit(2);
  }
}

if (require.main === module) main();
