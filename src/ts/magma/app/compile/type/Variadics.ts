import { Rule } from "../../../../magma/app/compile/rule/Rule";
import { TypeRule } from "../../../../magma/app/compile/rule/TypeRule";
import { StripRule } from "../../../../magma/app/compile/rule/StripRule";
import { SuffixRule } from "../../../../magma/app/compile/rule/SuffixRule";
import { NodeRule } from "../../../../magma/app/compile/rule/NodeRule";
export class Variadics {
	static createVariadicRule(typeRule: Rule): Rule {
		return new TypeRule("variadic", new StripRule(new SuffixRule("...", new NodeRule("child", typeRule))))/*unknown*/;
	}
}
