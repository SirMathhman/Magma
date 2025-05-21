import { Caller } from "../../../../magma/app/compile/value/Caller";
import { Value } from "../../../../magma/app/compile/value/Value";
import { Option } from "../../../../magma/api/option/Option";
import { None } from "../../../../magma/api/option/None";
export class ConstructionCaller implements Caller {
	right: string;
	constructor (right: string) {
		this.right = right;
	}
	generate(): string {
		return "new " + this.right/*unknown*/;
	}
	findChild(): Option<Value> {
		return new None<Value>()/*unknown*/;
	}
}
