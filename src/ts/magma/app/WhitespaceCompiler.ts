import { Rule } from "../../magma/app/compile/rule/Rule";
import { CompileState } from "../../magma/app/compile/CompileState";
import { Tuple2 } from "../../magma/api/Tuple2";
import { Option } from "../../magma/api/option/Option";
import { Whitespace } from "../../magma/app/compile/text/Whitespace";
import { Tuple2Impl } from "../../magma/api/Tuple2Impl";
import { Strings } from "../../magma/api/text/Strings";
import { Some } from "../../magma/api/option/Some";
import { None } from "../../magma/api/option/None";
export class WhitespaceRule implements Rule<string> {
	apply(state: CompileState, input: string): Option<Tuple2<CompileState, string>> {
		return WhitespaceCompiler.parseWhitespace(state, input).map((tuple: Tuple2<CompileState, Whitespace>) => new Tuple2Impl<CompileState, string>(tuple.left(), tuple.right().generate())/*unknown*/)/*unknown*/;
	}
}
export class WhitespaceCompiler {
	static createWhitespaceRule(): Rule<string> {
		return new WhitespaceRule()/*unknown*/;
	}
	static parseWhitespace(state: CompileState, input: string): Option<Tuple2<CompileState, Whitespace>> {
		if (Strings.isBlank(input)/*unknown*/){
			return new Some<Tuple2<CompileState, Whitespace>>(new Tuple2Impl<CompileState, Whitespace>(state, new Whitespace()))/*unknown*/;
		}
		return new None<Tuple2<CompileState, Whitespace>>()/*unknown*/;
	}
}
