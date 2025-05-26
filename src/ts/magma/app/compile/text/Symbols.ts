import { StripRule } from "../../../../magma/app/compile/rule/StripRule";
import { SymbolRule } from "../../../../magma/app/compile/rule/SymbolRule";
import { TypeRule } from "../../../../magma/app/compile/rule/TypeRule";
import { StringRule } from "../../../../magma/app/compile/rule/StringRule";
export class Symbols {
	static createSymbolRule(): StripRule {
		return new StripRule(new SymbolRule(new TypeRule("symbol", new StringRule("value"))))/*unknown*/;
	}
}
