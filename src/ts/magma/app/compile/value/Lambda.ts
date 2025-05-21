import { Value } from "../../../../magma/app/compile/value/Value";
import { Definition } from "../../../../magma/app/compile/define/Definition";
import { Iterable } from "../../../../magma/api/collect/list/Iterable";
import { Option } from "../../../../magma/api/option/Option";
import { None } from "../../../../magma/api/option/None";
export class Lambda implements Value {
	parameters: Iterable<Definition>;
	content: string;
	constructor (parameters: Iterable<Definition>, content: string) {
		this.parameters = parameters;
		this.content = content;
	}
	generateAsEnumValue(structureName: string): Option<string> {
		return new None<string>()/*unknown*/;
	}
}
