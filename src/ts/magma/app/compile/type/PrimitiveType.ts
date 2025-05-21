import { Type } from "../../../../magma/app/compile/type/Type";
import { TypeCompiler } from "../../../../magma/app/TypeCompiler";
export class PrimitiveType implements Type {
	static String: PrimitiveType = new PrimitiveType("string");
	static Number: PrimitiveType = new PrimitiveType("number");
	static Boolean: PrimitiveType = new PrimitiveType("boolean");
	static Var: PrimitiveType = new PrimitiveType("var");
	static Void: PrimitiveType = new PrimitiveType("void");
	static Unknown: PrimitiveType = new PrimitiveType("unknown");
	value: string;
	constructor (value: string) {
		this.value/*unknown*/ = value/*string*/;
	}
	isFunctional(): boolean {
		return false/*unknown*/;
	}
	isVar(): boolean {
		return PrimitiveType.Var === this/*unknown*/;
	}
	generateBeforeName(): string {
		return ""/*unknown*/;
	}
	generateSimple(): string {
		return TypeCompiler.generateType(this)/*unknown*/;
	}
}
