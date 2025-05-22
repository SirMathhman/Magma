import { CompileState } from "../../magma/app/compile/CompileState";
import { Tuple2 } from "../../magma/api/Tuple2";
import { OrRule } from "../../magma/app/compile/rule/OrRule";
import { Lists } from "../../jvm/api/collect/list/Lists";
import { WhitespaceCompiler } from "../../magma/app/WhitespaceCompiler";
import { StructureCompiler } from "../../magma/app/compile/structure/StructureCompiler";
import { Option } from "../../magma/api/option/Option";
import { Strings } from "../../magma/api/text/Strings";
import { Some } from "../../magma/api/option/Some";
import { Tuple2Impl } from "../../magma/api/Tuple2Impl";
import { None } from "../../magma/api/option/None";
import { FieldCompiler } from "../../magma/app/FieldCompiler";
import { Location } from "../../magma/app/Location";
import { FunctionSegmentCompiler } from "../../magma/app/FunctionSegmentCompiler";
import { Context } from "../../magma/app/compile/Context";
export class RootCompiler {
	static compileRootSegment(state: CompileState, input: string): Tuple2<CompileState, string> {
		return OrRule.compileOrPlaceholder(state, input, Lists.of(WhitespaceCompiler.compileWhitespace, RootCompiler.compileNamespaced, StructureCompiler.createStructureRule("class ", "class "), StructureCompiler.createStructureRule("interface ", "interface "), StructureCompiler.createStructureRule("record ", "class "), StructureCompiler.createStructureRule("enum ", "class ")))/*unknown*/;
	}
	static compileNamespaced(state: CompileState, input: string): Option<Tuple2<CompileState, string>> {
		let stripped = Strings.strip(input)/*unknown*/;
		if (stripped.startsWith("package ") || stripped.startsWith("import ")/*unknown*/){
			return new Some<Tuple2<CompileState, string>>(new Tuple2Impl<CompileState, string>(state, ""))/*unknown*/;
		}
		return new None<Tuple2<CompileState, string>>()/*unknown*/;
	}
	static compileClassSegment(state1: CompileState, input1: string): Tuple2<CompileState, string> {
		return OrRule.compileOrPlaceholder(state1, input1, Lists.of(WhitespaceCompiler.compileWhitespace, StructureCompiler.createStructureRule("class ", "class "), StructureCompiler.createStructureRule("interface ", "interface "), StructureCompiler.createStructureRule("record ", "class "), StructureCompiler.createStructureRule("enum ", "class "), FieldCompiler.compileMethod, FieldCompiler.compileFieldDefinition, FieldCompiler.compileEnumValues))/*unknown*/;
	}
	static compileRoot(state: CompileState, input: string, location: Location): Tuple2<CompileState, string> {
		return FunctionSegmentCompiler.compileStatements(state.mapContext((context2: Context) => context2.withLocation(location)/*unknown*/), input, RootCompiler.compileRootSegment)/*unknown*/;
	}
}
