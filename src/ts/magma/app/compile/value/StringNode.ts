import { Node } from "../../../../magma/app/compile/node/Node";
import { Option } from "../../../../magma/api/option/Option";
import { Some } from "../../../../magma/api/option/Some";
import { Type } from "../../../../magma/app/compile/type/Type";
import { CompileState } from "../../../../magma/app/compile/CompileState";
import { PrimitiveType } from "../../../../magma/app/compile/type/PrimitiveType";
export class StringNode implements Node {
	value: string;
	constructor (value: string) {
		this.value = value;
	}
	generate(): string {
		return "\"" + this.value + "\""/*unknown*/;
	}
	toNode(): Option<Node> {
		return new Some<Node>(this)/*unknown*/;
	}
	resolve(state: CompileState): Type {
		return PrimitiveType.Unknown/*unknown*/;
	}
	is(type: string): boolean {
		return false/*unknown*/;
	}
}
