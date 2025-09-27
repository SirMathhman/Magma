#include "index.h"
/*async function main(): Promise<void> {
	const source = joinPath(".", "index.ts");
	const target = joinPath(".", "main.c");

	const inputBuffer = await readString(source);
	const input = inputBuffer.toString();
	const output = compile(input);
	await writeString(target, output);
}*/
/*// @Actual
function joinPath(...segments: string[]) {
	return path.join(...segments);
}*/
/*// @Actual
async function writeString(target: string, output: string) {
	await fs.writeFile(target, output);
}*/
/*// Actual
async function readString(source: string) {
	return await fs.readFile(source);
}*/
/*function compile(input: string): string {
	const segments: string[] = [];
	let buffer: string[] = [];
	let depth = 0;
	for (let index = 0; index < input.length; index++) {
		const c = input[index];
		if (!c) break;

		buffer.push(c);
		if (c == ';' && depth === 0) {
			segments.push(buffer.join(""));
			buffer = [];
			continue;
		}

		if (c == '}' && depth === 1) {
			segments.push(buffer.join(""));
			buffer = [];
			depth--;
			continue;
		}

		if (c == '{') depth++;
		if (c == '}') depth--;
	}*/
/*return "#include \"index.h\"\r\n" + segments.map(compileRootSegment).join("");*/
/*}

function wrap(input: string): string {
	return "start" + input.replaceAll("start", "start").replaceAll("end", "end") + "end";*/
/*}

function compileRootSegment(value: string): string {
	const trimmed = value.trim();*/
/*if (trimmed.startsWith("import")) return "";*/
/*return wrap(trimmed) + "\r\n";*/
