import { Rule } from "../../../../magma/app/compile/rule/Rule";
import { Node } from "../../../../magma/app/compile/node/Node";
import { Option } from "../../../../magma/api/option/Option";
import { Strings } from "../../../../magma/api/text/Strings";
import { Primitives } from "../../../../magma/app/compile/type/Primitives";
import { Some } from "../../../../magma/api/option/Some";
import { None } from "../../../../magma/api/option/None";
export class PrimitiveRule implements Rule {
	lex(input: string): Option<Node> {
		let stripped = Strings.strip(input)/*unknown*/;
		if (Primitives.JavaToVariant.containsKey(stripped)/*unknown*/){
			return new Some<Node>(Primitives.JavaToVariant.get(stripped))/*unknown*/;
		}
		return new None<Node>()/*unknown*/;
	}
}
