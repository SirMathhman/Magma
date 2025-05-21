import { Type } from "../../../../../../magma/app/compile/type/Type";
import { Iterable } from "../../../../../../magma/api/collect/list/Iterable";
import { TypeCompiler } from "../../../../../../magma/app/TypeCompiler";
export class FunctionType implements Type {
	args: Iterable<string>;
	returns: string;
	constructor (args: Iterable<string>, returns: string) {
		this.args = args;
		this.returns = returns;
	}
	isFunctional(): boolean {
		return true/*unknown*/;
	}
	isVar(): boolean {
		return false/*unknown*/;
	}
	generateBeforeName(): string {
		return ""/*string*/;
	}
	generateSimple(): string {
		return TypeCompiler.generateType(this)/*unknown*/;
	}
}
