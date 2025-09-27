/*import * as path from "path";*//*
import * as fs from "fs/promises";*//*

await main();*//*

async function main() {
	const inputBuffer = await fs.readFile(path.join(".", "index.ts"));*//*
	const input = inputBuffer.toString();*//*
	await fs.writeFile(path.join(".", "main.c"), compile(input));*//*
}

function compile(input: string): string {
	const segments: string[] = [];*//*
	let buffer = "";*//*
	for (let index = 0;*//* index < input.length;*//* index++) {
		const c = input[index];*//*
		if (!c) break;*//*

		buffer += c;*//*
		if (c[0] == ';*//*') {
			segments.push(buffer);*//*
			buffer = "";*//*
		}
	}

	return segments.map(wrap).join("");*//*
}

function wrap(input: string): string {
	return "start" + input.replaceAll("start", "start").replaceAll("end", "end") + "end";*/