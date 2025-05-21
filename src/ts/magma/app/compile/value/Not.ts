import { Value } from "../../../../magma/app/compile/value/Value";
import { Option } from "../../../../magma/api/option/Option";
import { None } from "../../../../magma/api/option/None";
export class Not implements Value {
	child: string;
	constructor (child: string) {
		this.child = child;
	}
	generateAsEnumValue(structureName: string): Option<string> {
		return new None<string>()/*unknown*/;
	}
}
