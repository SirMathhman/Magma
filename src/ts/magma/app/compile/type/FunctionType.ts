import { Node } from "../../../../magma/app/compile/node/Node";
import { Iterable } from "../../../../magma/api/collect/list/Iterable";
export class FunctionType implements Node {
	args: Iterable<Node>;
	returns: Node;
	constructor (args: Iterable<Node>, returns: Node) {
		this.args/*unknown*/ = args/*Iterable<Node>*/;
		this.returns/*unknown*/ = returns/*Node*/;
	}
	static createFunctionType(args: Iterable<Node>, returns: Node): FunctionType {
		return new FunctionType(args, returns)/*unknown*/;
	}
	is(type: string): boolean {
		return "functional".equals(type)/*unknown*/;
	}
	args(): Iterable<Node> {
		return args/*Iterable<Node>*/;
	}
	returns(): Node {
		return returns/*Node*/;
	}
}
