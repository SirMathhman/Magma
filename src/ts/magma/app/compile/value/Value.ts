import { Argument } from "../../../../magma/app/compile/value/Argument";
import { Caller } from "../../../../magma/app/compile/value/Caller";
import { Option } from "../../../../magma/api/option/Option";
export interface Value extends Argument, Caller {
	generateAsEnumValue(structureName: string): Option<string>;
}
