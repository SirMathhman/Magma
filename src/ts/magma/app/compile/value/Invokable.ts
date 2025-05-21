import { Value } from "../../../../magma/app/compile/value/Value";
import { Caller } from "../../../../magma/app/compile/value/Caller";
import { Iterable } from "../../../../magma/api/collect/list/Iterable";
import { Option } from "../../../../magma/api/option/Option";
import { Some } from "../../../../magma/api/option/Some";
import { None } from "../../../../magma/api/option/None";
import { Type } from "../../../../magma/app/compile/type/Type";
import { CompileState } from "../../../../magma/app/compile/CompileState";
import { PrimitiveType } from "../../../../magma/app/compile/type/PrimitiveType";
import { ValueCompiler } from "../../../../magma/app/ValueCompiler";
export class Invokable implements Value {
	caller: Caller;
	args: Iterable<Value>;
	constructor (caller: Caller, args: Iterable<Value>) {
		this.caller = caller;
		this.args = args;
	}
	toValue(): Option<Value> {
		return new Some<Value>(this)/*unknown*/;
	}
	findChild(): Option<Value> {
		return new None<Value>()/*unknown*/;
	}
	resolve(state: CompileState): Type {
		return PrimitiveType.Unknown/*unknown*/;
	}
	generateAsEnumValue(structureName: string): Option<string> {
		return new Some<string>("\n\tstatic " + ValueCompiler.generateCaller(this.caller) + ": " + structureName + " = new " + structureName + "(" + ValueCompiler.joinArgs(this.args) + ");")/*unknown*/;
	}
}
