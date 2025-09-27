import * as path from "path";
import * as fs from "fs/promises";

const input = await fs.readFile(path.join(".", "index.ts"));
await fs.writeFile(path.join(".", "main.c"), input);