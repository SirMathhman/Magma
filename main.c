/*async function main() {
	const inputBuffer = await fs.readFile(path.join(".", "index.ts"));
	const input = inputBuffer.toString();
	await fs.writeFile(path.join(".", "main.c"), compile(input));
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
/*return segments.map(compileRootSegment).join("");*/
/*}

function wrap(input: string): string {
	return "start" + input.replaceAll("start", "start").replaceAll("end", "end") + "end";*/
/*}

function compileRootSegment(value: string): string {
	const trimmed = value.trim();*/
/*if (trimmed.startsWith("import")) return "";*/
/*return wrap(trimmed) + "\r\n";*/
