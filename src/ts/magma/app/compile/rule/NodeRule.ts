import { Node } from "../../../../magma/app/compile/node/Node";
import { Rule } from "../../../../magma/app/compile/rule/Rule";
import { Option } from "../../../../magma/api/option/Option";
import { MapNode } from "../../../../magma/app/compile/node/MapNode";
export class NodeRule implements Rule<Node> {
	key: string;
	rule: Rule<Node>;
	constructor (key: string, rule: Rule<Node>) {
		this.key = key;
		this.rule = rule;
	}
	lex(input: string): Option<Node> {
		return this.rule.lex(input).map(inner -  > new MapNode().withNode(this.key, inner))/*unknown*/;
	}
}
