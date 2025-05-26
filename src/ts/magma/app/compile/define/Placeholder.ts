import { Node } from "../../../../magma/app/compile/node/Node";
export class Placeholder implements Node {
	constructor () {
	}
	is(type: string): boolean {
		return false/*unknown*/;
	}
}
