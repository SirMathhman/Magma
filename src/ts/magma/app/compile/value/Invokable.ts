import { Value } from "../../../../magma/app/compile/value/Value";
import { Caller } from "../../../../magma/app/compile/value/Caller";
import { Iterable } from "../../../../magma/api/collect/list/Iterable";
import { Option } from "../../../../magma/api/option/Option";
import { Some } from "../../../../magma/api/option/Some";
import { ValueCompiler } from "../../../../magma/app/ValueCompiler";
export class Invokable implements Value {
	caller: Caller;
	args: Iterable<Value>;
	constructor (caller: Caller, args: Iterable<Value>) {
		this.caller = caller;
		this.args = args;
	}
	generateAsEnumValue(structureName: string): Option<string> {
		return new Some<string>("\n\tstatic " + ValueCompiler.generateCaller(this.caller) + ": " + structureName + " = new " + structureName + "(" + ValueCompiler.joinArgs(this.args) + ");")/*unknown*/;
	}
}
