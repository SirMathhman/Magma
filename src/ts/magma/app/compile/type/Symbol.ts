import { Type } from "../../../../magma/app/compile/type/Type";
import { CompileState } from "../../../../magma/app/compile/CompileState";
import { Definition } from "../../../../magma/app/compile/define/Definition";
import { PrimitiveType } from "../../../../magma/app/compile/type/PrimitiveType";
import { ValueCompiler } from "../../../../magma/app/ValueCompiler";
export class Symbol implements Type {
	value: string;
	constructor (value: string) {
		this.value = value;
	}
	generateType(): string {
		return this.value/*unknown*/;
	}
	resolve(state: CompileState): Type {
		return state.stack().resolveNode(this.value).map((definition: Definition) => {
			return definition.findType()/*unknown*/;
		}).orElse(PrimitiveType.Unknown)/*unknown*/;
	}
	generateBeforeName(): string {
		return ""/*unknown*/;
	}
	generateSimple(): string {
		return ValueCompiler.generateValue(this)/*unknown*/;
	}
	is(type: string): boolean {
		return false/*unknown*/;
	}
}
