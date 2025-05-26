import { Node } from "../../../../magma/app/compile/node/Node";
import { ValueCompiler } from "../../../../magma/app/ValueCompiler";
import { Option } from "../../../../magma/api/option/Option";
import { Some } from "../../../../magma/api/option/Some";
import { CompileState } from "../../../../magma/app/compile/CompileState";
import { Primitives } from "../../../../magma/app/compile/type/Primitives";
export class Operation implements Node {
	left: Node;
	targetInfix: string;
	right: Node;
	constructor (left: Node, targetInfix: string, right: Node) {
		this.left = left;
		this.targetInfix = targetInfix;
		this.right = right;
	}
	generate(): string {
		return ValueCompiler.generateValue(this.left) + " " + this.targetInfix + " " + ValueCompiler.generateValue(this.right)/*unknown*/;
	}
	toNode(): Option<Node> {
		return new Some<Node>(this)/*unknown*/;
	}
	resolve(state: CompileState): Node {
		return Primitives.UNKNOWN/*unknown*/;
	}
	is(type: string): boolean {
		return false/*unknown*/;
	}
}
