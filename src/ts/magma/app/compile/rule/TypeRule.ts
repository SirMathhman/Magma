import { Node } from "../../../../magma/app/compile/node/Node";
import { Rule } from "../../../../magma/app/compile/rule/Rule";
import { Option } from "../../../../magma/api/option/Option";
export class TypeRule implements Rule<Node> {
	type: string;
	childRule: Rule<Node>;
	constructor (type: string, childRule: Rule<Node>) {
		this.type = type;
		this.childRule = childRule;
	}
	lex(input: string): Option<Node> {
		return childRule.lex(input).map(result -  > result.retype(type))/*unknown*/;
	}
}
