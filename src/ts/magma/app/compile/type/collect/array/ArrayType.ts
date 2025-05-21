import { Type } from "../../../../../../magma/app/compile/type/Type";
export class ArrayType implements Type {
	childType: Type;
	constructor (childType: Type) {
		this.childType = childType;
	}
	isFunctional(): boolean {
		return false/*unknown*/;
	}
	isVar(): boolean {
		return false/*unknown*/;
	}
	generateBeforeName(): string {
		return ""/*string*/;
	}
	generateSimple(): string {
		return ""/*string*/;
	}
}
