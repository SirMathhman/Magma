import { Type } from "../../../../magma/app/compile/type/Type";
import { TypeCompiler } from "../../../../magma/app/TypeCompiler";
export class VariadicType implements Type {
	type: Type;
	constructor (type: Type) {
		this.type = type;
	}
	generateType(): string {
		return TypeCompiler.generateType(this.type) + "[]"/*unknown*/;
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
		return TypeCompiler.generateType(this)/*unknown*/;
	}
	is(type: string): boolean {
		return "variadic".equals(type)/*unknown*/;
	}
}
