import { CompileState } from "../../magma/app/compile/CompileState";
import { Tuple2 } from "../../magma/api/Tuple2";
import { Option } from "../../magma/api/option/Option";
import { Whitespace } from "../../magma/app/compile/text/Whitespace";
import { Tuple2Impl } from "../../magma/api/Tuple2Impl";
import { Strings } from "../../magma/api/text/Strings";
import { Some } from "../../magma/api/option/Some";
import { None } from "../../magma/api/option/None";
export class WhitespaceCompiler {
	static compileWhitespace(state: CompileState, input: string): Option<Tuple2<CompileState, string>> {
		return WhitespaceCompiler.parseWhitespace(state, input).map((tuple: Tuple2<CompileState, Whitespace>) => {
			return new Tuple2Impl<CompileState, string>(tuple.left(), "")/*unknown*/;
		})/*unknown*/;
	}
	static parseWhitespace(state: CompileState, input: string): Option<Tuple2<CompileState, Whitespace>> {
		if (Strings.isBlank(input)/*unknown*/){
			return new Some<Tuple2<CompileState, Whitespace>>(new Tuple2Impl<CompileState, Whitespace>(state, new Whitespace()))/*unknown*/;
		}
		return new None<Tuple2<CompileState, Whitespace>>()/*unknown*/;
	}
}
