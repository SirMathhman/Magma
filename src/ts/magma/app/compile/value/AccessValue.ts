import { Value } from "../../../../magma/app/compile/value/Value";
import { Option } from "../../../../magma/api/option/Option";
import { None } from "../../../../magma/api/option/None";
export class AccessValue implements Value {
	child: Value;
	property: string;
	constructor (child: Value, property: string) {
		this.child = child;
		this.property = property;
	}
	generateAsEnumValue(structureName: string): Option<string> {
		return new None<string>()/*unknown*/;
	}
}
