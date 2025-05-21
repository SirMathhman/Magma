import { Caller } from "../../../../magma/app/compile/value/Caller";
import { Value } from "../../../../magma/app/compile/value/Value";
import { Option } from "../../../../magma/api/option/Option";
import { None } from "../../../../magma/api/option/None";
export class ConstructionCaller implements Caller {
	type: string;
	constructor (type: string) {
		this.type = type;
	}
	generate(): string {
		return "new " + this.type/*unknown*/;
	}
	findChild(): Option<Value> {
		return new None<Value>()/*unknown*/;
	}
}
