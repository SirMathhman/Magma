import { Node } from "../../../../magma/app/compile/node/Node";
import { List } from "../../../../magma/api/collect/list/List";
import { Iterable } from "../../../../magma/api/collect/list/Iterable";
export class Invokable implements Node {
	node: Node;
	args: List<Node>;
	constructor (node: Node, args: List<Node>) {
		this.node/*unknown*/ = node/*Node*/;
		this.args/*unknown*/ = args/*List<Node>*/;
	}
	node(): Node {
		return node/*Node*/;
	}
	args(): Iterable<Node> {
		return args/*List<Node>*/;
	}
}
