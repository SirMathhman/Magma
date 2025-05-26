import { Node } from "../../../../magma/app/compile/node/Node";
import { Rule } from "../../../../magma/app/compile/rule/Rule";
import { Option } from "../../../../magma/api/option/Option";
import { Strings } from "../../../../magma/api/text/Strings";
export class StripRule implements Rule<Node> {
	childRule: Rule<Node>;
	constructor (childRule: Rule<Node>) {
		this.childRule = childRule;
	}
	lex(input: string): Option<Node> {
		return this.childRule.lex(Strings.strip(input))/*unknown*/;
	}
}
