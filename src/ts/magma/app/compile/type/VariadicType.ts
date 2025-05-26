import { Type } from "../../../../magma/app/compile/type/Type";
export class VariadicType implements Type {
	type: Type;
	constructor (type: Type) {
		this.type = type;
	}
	generate(): string {
		return this.type.generate() + "[]"/*unknown*/;
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
		return this.generate()/*unknown*/;
	}
}
