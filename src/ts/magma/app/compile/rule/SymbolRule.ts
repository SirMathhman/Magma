import { Node } from "../../../../magma/app/compile/node/Node";
import { Rule } from "../../../../magma/app/compile/rule/Rule";
import { TypeRule } from "../../../../magma/app/compile/rule/TypeRule";
import { HeadedIter } from "../../../../magma/api/collect/head/HeadedIter";
import { RangeHead } from "../../../../magma/api/collect/head/RangeHead";
import { Strings } from "../../../../magma/api/text/Strings";
import { Characters } from "../../../../magma/api/text/Characters";
import { Option } from "../../../../magma/api/option/Option";
import { None } from "../../../../magma/api/option/None";
export class SymbolRule implements Rule<Node> {
	childRule: TypeRule;
	constructor (childRule: TypeRule) {
		this.childRule = childRule;
	}
	static isSymbol(input: string): boolean {
		let query = new HeadedIter<number>(new RangeHead(Strings.length(input)))/*unknown*/;
		return query.allMatch((index: number) => {
			return SymbolRule.isSymbolChar(index, input.charAt(index))/*unknown*/;
		})/*unknown*/;
	}
	static isSymbolChar(index: number, c: string): boolean {
		return "_" === c || Characters.isLetter(c) || (0 !== index && Characters.isDigit(c))/*unknown*/;
	}
	lex(input: string): Option<Node> {
		if (SymbolRule.isSymbol(input)/*unknown*/){
			return this.childRule().lex(input)/*unknown*/;
		}
		return new None<?>()/*unknown*/;
	}
}
