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
	retype(type: string): Node {
		return this/*unknown*/;
	}
	withNode(key: string, value: Node): Node {
		return this/*unknown*/;
	}
	withString(key: string, value: string): Node {
		return this/*unknown*/;
	}
	withNodeList(key: string, values: List<Node>): Node {
		return this/*unknown*/;
	}
}
