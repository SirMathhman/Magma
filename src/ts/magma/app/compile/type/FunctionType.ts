import { Type } from "../../../../magma/app/compile/type/Type";
import { Iterable } from "../../../../magma/api/collect/list/Iterable";
import { Tuple2 } from "../../../../magma/api/Tuple2";
import { Joiner } from "../../../../magma/api/collect/Joiner";
import { TypeCompiler } from "../../../../magma/app/TypeCompiler";
export class FunctionType implements Type {
	args: Iterable<string>;
	returns: string;
	constructor (args: Iterable<string>, returns: string) {
		this.args = args;
		this.returns = returns;
	}
	generateType(): string {
		let joinedArguments = this.args.iterWithIndices().map((tuple: Tuple2<number, string>) => {
			return "arg" + tuple.left() + " : " + tuple.right()/*unknown*/;
		}).collect(new Joiner(", ")).orElse("")/*unknown*/;
		return "(" + joinedArguments + ") => " + this.returns/*unknown*/;
	}
	isFunctional(): boolean {
		return true/*unknown*/;
	}
	isVar(): boolean {
		return false/*unknown*/;
	}
	generateBeforeName(): string {
		return ""/*unknown*/;
	}
	generateSimple(): string {
		return TypeCompiler.generateType(this)/*unknown*/;
	}
	is(type: string): boolean {
		return "functional".equals(type)/*unknown*/;
	}
}
