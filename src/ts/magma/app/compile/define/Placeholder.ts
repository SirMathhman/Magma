import { Node } from "../../../../magma/app/compile/node/Node";
export class Placeholder implements Node {
	input: string;
	constructor (input: string) {
		this.input = input;
	}
	static generatePlaceholder(input: string): string {
		let replaced = input.replace("/*", "start").replace("*/", "end")/*unknown*/;
		return "/*" + replaced + "*/"/*unknown*/;
	}
	is(type: string): boolean {
		return false/*unknown*/;
	}
}
