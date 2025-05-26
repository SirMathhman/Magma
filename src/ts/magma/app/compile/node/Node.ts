import { Option } from "../../../../magma/api/option/Option";
import { None } from "../../../../magma/api/option/None";
export interface Node {
	is(type: string): boolean {
		return false/*unknown*/;
	}
	findNode(key: string): Option<Node> {
		return new None<>()/*unknown*/;
	}
	findString(key: string): Option<string> {
		return new None<>()/*unknown*/;
	}
}
