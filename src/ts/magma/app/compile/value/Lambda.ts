import { Node } from "../../../../magma/app/compile/node/Node";
import { Definition } from "../../../../magma/app/compile/define/Definition";
import { Iterable } from "../../../../magma/api/collect/list/Iterable";
import { DefiningCompiler } from "../../../../magma/app/DefiningCompiler";
import { Joiner } from "../../../../magma/api/collect/Joiner";
import { CompileState } from "../../../../magma/app/compile/CompileState";
import { Primitives } from "../../../../magma/app/compile/type/Primitives";
export class Lambda implements Node {
	paramNames: Iterable<Definition>;
	content: string;
	constructor (paramNames: Iterable<Definition>, content: string) {
		this.paramNames = paramNames;
		this.content = content;
	}
	generate(): string {
		let joinedParamNames = this.paramNames.iter().map((definition: Definition) => {
			return DefiningCompiler.generateParameter(definition)/*unknown*/;
		}).collect(new Joiner(", ")).orElse("")/*unknown*/;
		return "(" + joinedParamNames + ")" + " => " + this.content/*unknown*/;
	}
	resolve(state: CompileState): Node {
		return Primitives.UNKNOWN/*unknown*/;
	}
	is(type: string): boolean {
		return false/*unknown*/;
	}
}
