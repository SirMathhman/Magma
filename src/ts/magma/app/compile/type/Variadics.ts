import { Node } from "../../../../magma/app/compile/node/Node";
import { Rule } from "../../../../magma/app/compile/rule/Rule";
import { TypeRule } from "../../../../magma/app/compile/rule/TypeRule";
import { StripRule } from "../../../../magma/app/compile/rule/StripRule";
import { SuffixRule } from "../../../../magma/app/compile/rule/SuffixRule";
import { NodeRule } from "../../../../magma/app/compile/rule/NodeRule";
import { TypeCompiler } from "../../../../magma/app/TypeCompiler";
export class Variadics {
	static createVariadicRule(): Rule<Node> {
		return new TypeRule("variadic", new StripRule(new SuffixRule<Node>("...", new NodeRule("child", TypeCompiler.lexType))))/*unknown*/;
	}
}
