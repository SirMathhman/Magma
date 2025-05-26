import { Node } from "../../../../magma/app/compile/node/Node";
import { Rule } from "../../../../magma/app/compile/rule/Rule";
import { Option } from "../../../../magma/api/option/Option";
import { Some } from "../../../../magma/api/option/Some";
import { MapNode } from "../../../../magma/app/compile/node/MapNode";
export class StringRule implements Rule<Node> {
	key: string;
	constructor (key: string) {
		this.key = key;
	}
	lex(input: string): Option<Node> {
		return new Some<?>(new MapNode().withString(this.key, input))/*unknown*/;
	}
}
