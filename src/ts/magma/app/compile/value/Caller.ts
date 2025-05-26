import { Value } from "../../../../magma/app/compile/value/Value";
import { Option } from "../../../../magma/api/option/Option";
import { None } from "../../../../magma/api/option/None";
export interface Caller {
	generate(): string;
	findChild(): Option<Value> {
		return new None<Value>()/*unknown*/;
	}
}
