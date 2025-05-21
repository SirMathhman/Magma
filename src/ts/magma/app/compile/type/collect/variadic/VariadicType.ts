import { Type } from "../../../../../../magma/app/compile/type/Type";
import { TypeCompiler } from "../../../../../../magma/app/TypeCompiler";
export class VariadicType implements Type {
	type: Type;
	constructor (type: Type) {
		this.type = type;
	}
	isFunctional(): boolean {
		return false/*unknown*/;
	}
	isVar(): boolean {
		return false/*unknown*/;
	}
	generateBeforeName(): string {
		return "..."/*string*/;
	}
	generateSimple(): string {
		return TypeCompiler.generateType(this)/*unknown*/;
	}
}
