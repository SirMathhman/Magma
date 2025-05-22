import { Rule } from "../../../../magma/app/compile/rule/Rule";
import { CompileState } from "../../../../magma/app/compile/CompileState";
import { Tuple2 } from "../../../../magma/api/Tuple2";
import { Option } from "../../../../magma/api/option/Option";
import { Strings } from "../../../../magma/api/text/Strings";
import { Some } from "../../../../magma/api/option/Some";
import { Tuple2Impl } from "../../../../magma/api/Tuple2Impl";
import { None } from "../../../../magma/api/option/None";
export class NamespacedRule implements Rule<string> {
	apply(state: CompileState, input: string): Option<Tuple2<CompileState, string>> {
		let stripped = Strings.strip(input)/*unknown*/;
		if (stripped.startsWith("package ") || stripped.startsWith("import ")/*unknown*/){
			return new Some<Tuple2<CompileState, string>>(new Tuple2Impl<CompileState, string>(state, ""))/*unknown*/;
		}
		return new None<Tuple2<CompileState, string>>()/*unknown*/;
	}
}
