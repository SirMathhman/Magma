import { Value } from "../../../../magma/app/compile/value/Value";
import { Caller } from "../../../../magma/app/compile/value/Caller";
import { Iterable } from "../../../../magma/api/collect/list/Iterable";
export class Invokable implements Value {
	caller: Caller;
	args: Iterable<Value>;
	constructor (caller: Caller, args: Iterable<Value>) {
		this.caller = caller;
		this.args = args;
	}
}
