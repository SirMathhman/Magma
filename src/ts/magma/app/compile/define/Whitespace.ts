import { Definition } from "../../../../magma/app/compile/define/Definition";
import { Option } from "../../../../magma/api/option/Option";
import { None } from "../../../../magma/api/option/None";
export class Whitespace {
	generate(): string {
		return ""/*unknown*/;
	}
	asDefinition(): Option<Definition> {
		return new None<Definition>()/*unknown*/;
	}
	is(type: string): boolean {
		return "whitespace".equals(type)/*unknown*/;
	}
}
