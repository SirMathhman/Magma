import { Node } from "../../../../magma/app/compile/node/Node";
export class Access implements Node {
	child: Node;
	property: string;
	constructor (child: Node, property: string) {
		this.child = child;
		this.property = property;
	}
}
