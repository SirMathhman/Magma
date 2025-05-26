import { Node } from "../../../../magma/app/compile/node/Node";
import { Iterable } from "../../../../magma/api/collect/list/Iterable";
import { ValueCompiler } from "../../../../magma/app/ValueCompiler";
import { Joiner } from "../../../../magma/api/collect/Joiner";
import { Option } from "../../../../magma/api/option/Option";
import { Some } from "../../../../magma/api/option/Some";
import { Type } from "../../../../magma/app/compile/type/Type";
import { CompileState } from "../../../../magma/app/compile/CompileState";
import { PrimitiveType } from "../../../../magma/app/compile/type/PrimitiveType";
export class Invokable implements Node {
	node: Node;
	args: Iterable<Node>;
	constructor (node: Node, args: Iterable<Node>) {
		this.node = node;
		this.args = args;
	}
	generate(): string {
		let joinedArguments = this.joinArgs()/*unknown*/;
		return ValueCompiler.getString(this.node) + "(" + joinedArguments + ")"/*unknown*/;
	}
	joinArgs(): string {
		return this.args.iter().map((value: Node) => {
			return ValueCompiler.generateValue(value)/*unknown*/;
		}).collect(new Joiner(", ")).orElse("")/*unknown*/;
	}
	toNode(): Option<Node> {
		return new Some<Node>(this)/*unknown*/;
	}
	resolve(state: CompileState): Type {
		return PrimitiveType.Unknown/*unknown*/;
	}
}
