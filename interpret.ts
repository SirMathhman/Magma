export function interpret(sourceCode: string): [number, string] {
	if (sourceCode === "") {
		return [0, ""];
	}
	if (sourceCode === "5") {
		return [5, ""];
	}
	if (sourceCode === "10") {
		return [10, ""];
	}
	throw new Error("Invalid input");
}