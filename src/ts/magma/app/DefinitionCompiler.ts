import { Definition } from "../../magma/app/compile/define/Definition";
import { Iterable } from "../../magma/api/collect/list/Iterable";
import { Parameter } from "../../magma/app/compile/define/Parameter";
import { Iters } from "../../magma/api/collect/Iters";
import { ListCollector } from "../../magma/api/collect/list/ListCollector";
import { Joiner } from "../../magma/api/collect/Joiner";
import { CompileState } from "../../magma/app/compile/CompileState";
import { List } from "../../magma/api/collect/list/List";
import { Tuple2 } from "../../magma/api/Tuple2";
import { ValueCompiler } from "../../magma/app/ValueCompiler";
import { Some } from "../../magma/api/option/Some";
import { DefiningCompiler } from "../../magma/app/DefiningCompiler";
import { Tuple2Impl } from "../../magma/api/Tuple2Impl";
import { Lists } from "../../jvm/api/collect/list/Lists";
class DefinitionCompiler {
	static retainDefinitionsFromParameters(parameters: Iterable<Parameter>): Iterable<Definition> {
		return parameters.iter().map((parameter: Parameter) => {
			return parameter.asDefinition()/*unknown*/;
		}).flatMap(Iters.fromOption).collect(new ListCollector<Definition>())/*unknown*/;
	}
	static joinParameters(parameters: Iterable<Definition>): string {
		return parameters.iter().map((definition: Definition) => {
			return definition.generate()/*unknown*/;
		}).map((generated: string) => {
			return "\n\t" + generated + ";"/*unknown*/;
		}).collect(Joiner.empty()).orElse("")/*unknown*/;
	}
	static parseParameters(state: CompileState, params: string): Tuple2<CompileState, List<Parameter>> {
		return ValueCompiler.values((state1: CompileState, s: string) => {
			return new Some<Tuple2<CompileState, Parameter>>(DefiningCompiler.parseParameterOrPlaceholder(state1, s))/*unknown*/;
		}).apply(state, params).orElse(new Tuple2Impl<CompileState, List<Parameter>>(state, Lists.empty()))/*unknown*/;
	}
}
