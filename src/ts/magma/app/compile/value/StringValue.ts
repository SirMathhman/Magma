import { Value } from "../../../../magma/app/compile/value/Value";
import { Option } from "../../../../magma/api/option/Option";
import { None } from "../../../../magma/api/option/None";
export class StringValue implements Value {
	value: string;
	constructor (value: string) {
		this.value = value;
	}
	findChild(): Option<Value> {
		return new None<Value>()/*unknown*/;
	}
	generateAsEnumValue(structureName: string): Option<string> {
		return new None<string>()/*unknown*/;
	}
}
