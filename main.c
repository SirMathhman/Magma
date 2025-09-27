/*import * as path from "path";
import * as fs from "fs/promises";

await main();

async function main() {
	const inputBuffer = await fs.readFile(path.join(".", "index.ts"));
	const input = inputBuffer.toString();
	await fs.writeFile(path.join(".", "main.c"), wrap(input));
}

function wrap(input: string): string {
	return "start" + input.replaceAll("start", "start").replaceAll("end", "end") + "end";
}

*/