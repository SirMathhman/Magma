import { Node } from "../../../../magma/app/compile/node/Node";
import { ValueCompiler } from "../../../../magma/app/ValueCompiler";
import { Option } from "../../../../magma/api/option/Option";
import { Some } from "../../../../magma/api/option/Some";
import { Type } from "../../../../magma/app/compile/type/Type";
import { CompileState } from "../../../../magma/app/compile/CompileState";
import { PrimitiveType } from "../../../../magma/app/compile/type/PrimitiveType";
export class Access implements Node {
	child: Node;
	property: string;
	constructor (child: Node, property: string) {
		this.child = child;
		this.property = property;
	}
	generate(): string {
		return ValueCompiler.generateValue(this.child) + "." + this.property/*unknown*/;
	}
	toNode(): Option<Node> {
		return new Some<Node>(this)/*unknown*/;
	}
	resolve(state: CompileState): Type {
		return PrimitiveType.Unknown/*unknown*/;
	}
}
