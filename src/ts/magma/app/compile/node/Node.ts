import { Option } from "../../../../magma/api/option/Option";
import { None } from "../../../../magma/api/option/None";
import { List } from "../../../../magma/api/collect/list/List";
export interface Node {
	findNode(key: string): Option<Node> {
		return new None<?>()/*unknown*/;
	}
	findString(key: string): Option<string> {
		return new None<?>()/*unknown*/;
	}
	findNodeList(key: string): Option<List<Node>> {
		return new None<?>()/*unknown*/;
	}
	is(type: string): boolean;
}
