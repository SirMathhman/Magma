#include "index.h"
void empty(){}
main()(empty);
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
/*let joined: string[] = []*/;
/*segments.forEach(segment => {
		const compiled = compileRootSegment(segment);
		joined.push(...compiled[1]);
		joined.push(compiled[0]);
	}*/
/*)*/;
/*return "#include \"index.h\"\r\n" + joined.join("")*/;
/*}

function wrap(input: string): string {
	return "start" + input.replaceAll("start", "start").replaceAll("end", "end") + "end"*/;
/*}

function compileRootSegment(value: string): [string, string[]] {
	const trimmed = value.trim()*/;
/*if (trimmed.startsWith("import")) return ["", []]*/;
/*const result = compileRootSegmentValue(trimmed)*/;
/*return [result[0] + "\r\n", result[1]]*/;
/*}

function compileRootSegmentValue(input: string): [string, string[]] {
	if (input.endsWith("*/;
/*")) {
		const slice = input.substring(0, input.length - ";".length);
		const result = compileRootStatementValue(slice);
		return [result[0] + ";", result[1]];
	}*/
/*return [wrap(input), []]*/;
/*}

function compileRootStatementValue(input: string): [string, string[]] {
	if (input.startsWith("await ")) {
		const result = input.substring("await ".length);
		return [compileExpression(result) + "(empty)", ["void empty(){}\r\n"]];
	}*/
/*return [wrap(input), []]*/;
/*}

function compileExpression(input: string): string {
	if (input.endsWith("()")) {
		const inner = input.substring(0, input.length - 2);
		return compileExpression(inner) + "()";
	}*/
/*if (/^[a-zA-Z_$][a-zA-Z0-9_$]*$/.test(input)) {
		return input;
	}*/
/*return wrap(input)*/;
