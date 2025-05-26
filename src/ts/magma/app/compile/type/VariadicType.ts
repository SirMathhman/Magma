import { Node } from "../../../../magma/app/compile/node/Node";
import { TypeCompiler } from "../../../../magma/app/TypeCompiler";
export class VariadicType implements Node {
	type: Node;
	constructor (type: Node) {
		this.type = type;
	}
	generateNode(): string {
		return TypeCompiler.generateNode(this.type) + "[]"/*unknown*/;
	}
	isFunctional(): boolean {
		return false/*unknown*/;
	}
	isVar(): boolean {
		return false/*unknown*/;
	}
	generateBeforeName(): string {
		return "..."/*unknown*/;
	}
	generateSimple(): string {
		return TypeCompiler.generateNode(this)/*unknown*/;
	}
	is(type: string): boolean {
		return "variadic".equals(type)/*unknown*/;
	}
}
