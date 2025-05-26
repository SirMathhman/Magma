import { Node } from "../../../../magma/app/compile/node/Node";
import { Definition } from "../../../../magma/app/compile/define/Definition";
import { Iterable } from "../../../../magma/api/collect/list/Iterable";
import { Joiner } from "../../../../magma/api/collect/Joiner";
import { Option } from "../../../../magma/api/option/Option";
import { Some } from "../../../../magma/api/option/Some";
import { Type } from "../../../../magma/app/compile/type/Type";
import { CompileState } from "../../../../magma/app/compile/CompileState";
import { PrimitiveType } from "../../../../magma/app/compile/type/PrimitiveType";
export class Lambda implements Node {
	paramNames: Iterable<Definition>;
	content: string;
	constructor (paramNames: Iterable<Definition>, content: string) {
		this.paramNames = paramNames;
		this.content = content;
	}
	generate(): string {
		let joinedParamNames = this.paramNames.iter().map((definition: Definition) => {
			return definition.generate()/*unknown*/;
		}).collect(new Joiner(", ")).orElse("")/*unknown*/;
		return "(" + joinedParamNames + ")" + " => " + this.content/*unknown*/;
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
