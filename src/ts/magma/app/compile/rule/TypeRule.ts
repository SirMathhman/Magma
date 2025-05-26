import { Rule } from "../../../../magma/app/compile/rule/Rule";
import { Node } from "../../../../magma/app/compile/node/Node";
import { Option } from "../../../../magma/api/option/Option";
export class TypeRule implements Rule {
	type: string;
	childRule: Rule;
	constructor (type: string, childRule: Rule) {
		this.type = type;
		this.childRule = childRule;
	}
	lex(input: string): Option<Node> {
		return childRule.lex(input).map(result -  > result.retype(type))/*unknown*/;
	}
}
