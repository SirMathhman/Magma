import { Value } from "../../../../magma/app/compile/value/Value";
import { Caller } from "../../../../magma/app/compile/value/Caller";
import { Iterable } from "../../../../magma/api/collect/list/Iterable";
import { Joiner } from "../../../../magma/api/collect/Joiner";
import { Type } from "../../../../magma/app/compile/type/Type";
import { CompileState } from "../../../../magma/app/compile/CompileState";
import { PrimitiveType } from "../../../../magma/app/compile/type/PrimitiveType";
import { Option } from "../../../../magma/api/option/Option";
import { Some } from "../../../../magma/api/option/Some";
export class Invokable implements Value {
	caller: Caller;
	args: Iterable<Value>;
	constructor (caller: Caller, args: Iterable<Value>) {
		this.caller = caller;
		this.args = args;
	}
	generate(): string {
		let joinedArguments = this.joinArgs()/*unknown*/;
		return this.caller.generate() + "(" + joinedArguments + ")"/*unknown*/;
	}
	joinArgs(): string {
		return this.args.iter().map((value: Value) => {
			return value.generate()/*unknown*/;
		}).collect(new Joiner(", ")).orElse("")/*unknown*/;
	}
	resolve(state: CompileState): Type {
		return PrimitiveType.Unknown/*unknown*/;
	}
	generateAsEnumValue(structureName: string): Option<string> {
		return new Some<string>("\n\tstatic " + this.caller.generate() + ": " + structureName + " = new " + structureName + "(" + this.joinArgs() + ");")/*unknown*/;
	}
}
