import { Rule } from "../../../../magma/app/compile/rule/Rule";
import { Node } from "../../../../magma/app/compile/node/Node";
import { Option } from "../../../../magma/api/option/Option";
import { MapNode } from "../../../../magma/app/compile/node/MapNode";
export class NodeRule implements Rule {
	key: string;
	childRule: Rule;
	constructor (key: string, childRule: Rule) {
		this.key = key;
		this.childRule = childRule;
	}
	lex(input: string): Option<Node> {
		return this.childRule.lex(input).map(inner -  > new MapNode().withNode(this.key, inner))/*unknown*/;
	}
}
