import { Rule } from "../../../../magma/app/compile/rule/Rule";
import { Node } from "../../../../magma/app/compile/node/Node";
import { Option } from "../../../../magma/api/option/Option";
import { None } from "../../../../magma/api/option/None";
export class SuffixRule implements Rule {
	suffix: string;
	rule: Rule;
	constructor (suffix: string, rule: Rule) {
		this.suffix = suffix;
		this.rule = rule;
	}
	lex(input: string): Option<Node> {
		if (input.endsWith(this.suffix)/*unknown*/){
			return this.rule.lex(input.substring(0, input.length() - this.suffix.length()))/*unknown*/;
		}
		else {
			return new None<?>()/*unknown*/;
		}
	}
}
