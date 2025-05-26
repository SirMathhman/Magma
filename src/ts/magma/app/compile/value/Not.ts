import { Node } from "../../../../magma/app/compile/node/Node";
import { Option } from "../../../../magma/api/option/Option";
import { Some } from "../../../../magma/api/option/Some";
import { CompileState } from "../../../../magma/app/compile/CompileState";
import { TypeCompiler } from "../../../../magma/app/TypeCompiler";
export class Not implements Node {
	child: string;
	constructor (child: string) {
		this.child = child;
	}
	generate(): string {
		return this.child/*unknown*/;
	}
	toNode(): Option<Node> {
		return new Some<Node>(this)/*unknown*/;
	}
	resolve(state: CompileState): Node {
		return TypeCompiler.Unknown/*unknown*/;
	}
	is(type: string): boolean {
		return false/*unknown*/;
	}
}
