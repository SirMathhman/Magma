import { Node } from "../../../../magma/app/compile/node/Node";
import { Option } from "../../../../magma/api/option/Option";
import { Some } from "../../../../magma/api/option/Some";
import { None } from "../../../../magma/api/option/None";
export class MapNode implements Node {
	type: Option<string>;
	strings: Map<string, string>;
	nodes: Map<string, Node>;
	constructor (type: Option<string>, strings: Map<string, string>, nodes: Map<string, Node>) {
		this.type = type;
		this.strings = strings;
		this.nodes = nodes;
	}
	constructor (type: string) {
		this(new Some<>(type), new HashMap<>(), new HashMap<>())/*unknown*/;
	}
	constructor () {
		this(new None<>(), new HashMap<>(), new HashMap<>())/*unknown*/;
	}
	is(type: string): boolean {
		return this.type.filter((inner: string) => inner.equals(type)/*unknown*/).isPresent()/*unknown*/;
	}
	findNode(key: string): Option<Node> {
		if (this.nodes.containsKey(key)/*unknown*/){
			return new Some<>(this.nodes.get(key))/*unknown*/;
		}
		else {
			return new None<>()/*unknown*/;
		}
	}
	findString(key: string): Option<string> {
		if (this.strings.containsKey(key)/*unknown*/){
			return new Some<>(this.strings.get(key))/*unknown*/;
		}
		else {
			return new None<>()/*unknown*/;
		}
	}
	withNode(key: string, value: Node): MapNode {
		this.nodes.put(key, value)/*unknown*/;
		return this/*unknown*/;
	}
	withString(key: string, value: string): MapNode {
		this.strings.put(key, value)/*unknown*/;
		return this/*unknown*/;
	}
}
