import { Node } from "../../../../magma/app/compile/node/Node";
export class PrimitiveType implements Node {
	constructor ();
	constructor ();
	constructor ();
	constructor ();
	constructor ();
	constructor ();
	value: string;
	constructor (value: string) {
		this.value/*unknown*/ = value/*string*/;
	}
	is(type: string): boolean {
		return type.equals(this.value)/*unknown*/;
	}
}
