import { Rule } from "../../../../magma/app/compile/rule/Rule";
import { Option } from "../../../../magma/api/option/Option";
import { None } from "../../../../magma/api/option/None";
import { Strings } from "../../../../magma/api/text/Strings";
export class SuffixRule<T> implements Rule<T> {
	suffix: string;
	childRule: Rule<T>;
	constructor (suffix: string, childRule: Rule<T>) {
		this.suffix = suffix;
		this.childRule = childRule;
	}
	lex(input: string): Option<T> {
		if (!input/*string*/.endsWith(this.suffix)/*unknown*/){
			return new None<T>()/*unknown*/;
		}
		let length = Strings.length(input)/*unknown*/;
		let length1 = Strings.length(this.suffix)/*unknown*/;
		let content = Strings.sliceBetween(input, 0, length - length1)/*unknown*/;
		return this.childRule.lex(content)/*unknown*/;
	}
}
