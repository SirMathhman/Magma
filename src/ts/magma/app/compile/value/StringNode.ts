import { Node } from "../../../../magma/app/compile/node/Node";
export class StringNode implements Node {
	value: string;
	constructor (value: string) {
		this.value = value;
	}
	generate(): string {
		return "\"" + this.value + "\""/*unknown*/;
	}
	is(type: string): boolean {
		return false/*unknown*/;
	}
}
