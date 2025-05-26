import { Type } from "../../../../magma/app/compile/type/Type";
import { CompileState } from "../../../../magma/app/compile/CompileState";
import { Definition } from "../../../../magma/app/compile/define/Definition";
import { PrimitiveType } from "../../../../magma/app/compile/type/PrimitiveType";
import { Node } from "../../../../magma/app/compile/node/Node";
import { Option } from "../../../../magma/api/option/Option";
import { Some } from "../../../../magma/api/option/Some";
import { ValueCompiler } from "../../../../magma/app/ValueCompiler";
export class Symbol {
	value: string;
	constructor (value: string) {
		this.value = value;
	}
	generate(): string {
		return this.value/*unknown*/;
	}
	resolve(state: CompileState): Type {
		return state.stack().resolveNode(this.value).map((definition: Definition) => {
			return definition.findType()/*unknown*/;
		}).orElse(PrimitiveType.Unknown)/*unknown*/;
	}
	toNode(): Option<Node> {
		return new Some<Node>(this)/*unknown*/;
	}
	isFunctional(): boolean {
		return false/*unknown*/;
	}
	isVar(): boolean {
		return false/*unknown*/;
	}
	generateBeforeName(): string {
		return ""/*unknown*/;
	}
	generateSimple(): string {
		return ValueCompiler.generateValue(this)/*unknown*/;
	}
}
