export class Placeholder {
	input: string;
	constructor (input: string) {
		this.input = input;
	}
	static generatePlaceholder(input: string): string {
		let replaced = input.replace("/*", "start").replace("*/", "end")/*unknown*/;
		return "/*" + replaced + "*/"/*unknown*/;
	}
	generate(): string {
		return Placeholder.generatePlaceholder(this.input)/*unknown*/;
	}
	is(type: string): boolean {
		return false/*unknown*/;
	}
}
