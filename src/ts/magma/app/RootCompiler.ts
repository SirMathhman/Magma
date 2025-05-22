import { Rule } from "../../magma/app/compile/rule/Rule";
import { OrRule } from "../../magma/app/compile/rule/OrRule";
import { Lists } from "../../jvm/api/collect/list/Lists";
import { WhitespaceCompiler } from "../../magma/app/WhitespaceCompiler";
import { NamespacedRule } from "../../magma/app/compile/namespace/NamespacedRule";
import { StructureCompiler } from "../../magma/app/compile/structure/StructureCompiler";
import { CompileState } from "../../magma/app/compile/CompileState";
import { Tuple2 } from "../../magma/api/Tuple2";
import { FieldCompiler } from "../../magma/app/FieldCompiler";
import { Location } from "../../magma/app/Location";
import { FunctionSegmentCompiler } from "../../magma/app/FunctionSegmentCompiler";
import { Context } from "../../magma/app/compile/Context";
import { Tuple2Impl } from "../../magma/api/Tuple2Impl";
import { Placeholder } from "../../magma/app/compile/value/Placeholder";
export class RootCompiler {
	static createRootSegmentRule(): Rule<string> {
		return new OrRule<string>(Lists.of(WhitespaceCompiler.createWhitespaceRule(), new NamespacedRule(), StructureCompiler.createStructureRule("class ", "class "), StructureCompiler.createStructureRule("interface ", "interface "), StructureCompiler.createStructureRule("record ", "class "), StructureCompiler.createStructureRule("enum ", "class ")))/*unknown*/;
	}
	static compileClassSegment(state1: CompileState, input1: string): Tuple2<CompileState, string> {
		return OrRule.compileOrPlaceholder(state1, input1, Lists.of(WhitespaceCompiler.createWhitespaceRule(), StructureCompiler.createStructureRule("class ", "class "), StructureCompiler.createStructureRule("interface ", "interface "), StructureCompiler.createStructureRule("record ", "class "), StructureCompiler.createStructureRule("enum ", "class "), FieldCompiler.createMethodRule(), FieldCompiler.createFieldDefinitionRule(), FieldCompiler.createEnumValuesRule()))/*unknown*/;
	}
	static compileRoot(state: CompileState, input: string, location: Location): Tuple2<CompileState, string> {
		return FunctionSegmentCompiler.compileStatements(state.mapContext((context2: Context) => context2.withLocation(location)/*unknown*/), input, (state1: CompileState, input1: string) => RootCompiler.createRootSegmentRule().apply(state1, input1).orElseGet(() => new Tuple2Impl<CompileState, string>(state1, Placeholder.generatePlaceholder(input1))/*unknown*/)/*unknown*/)/*unknown*/;
	}
}
