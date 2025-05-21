import { CompileState } from "../../magma/app/compile/CompileState";
import { Tuple2 } from "../../magma/api/Tuple2";
import { Option } from "../../magma/api/option/Option";
import { LocatingSplitter } from "../../magma/app/compile/split/LocatingSplitter";
import { FirstLocator } from "../../magma/app/compile/locate/FirstLocator";
import { Splitter } from "../../magma/app/compile/split/Splitter";
import { SplitComposable } from "../../magma/app/compile/compose/SplitComposable";
import { Composable } from "../../magma/app/compile/compose/Composable";
import { Strings } from "../../magma/api/text/Strings";
import { ConstructorHeader } from "../../magma/app/compile/define/ConstructorHeader";
import { None } from "../../magma/api/option/None";
import { DefiningCompiler } from "../../magma/app/DefiningCompiler";
import { Definition } from "../../magma/app/compile/define/Definition";
import { MethodHeader } from "../../magma/app/compile/define/MethodHeader";
import { Joiner } from "../../magma/api/collect/Joiner";
import { Some } from "../../magma/api/option/Some";
import { Tuple2Impl } from "../../magma/api/Tuple2Impl";
import { PrefixComposable } from "../../magma/app/compile/compose/PrefixComposable";
import { SuffixComposable } from "../../magma/app/compile/compose/SuffixComposable";
import { FunctionSegmentCompiler } from "../../magma/app/FunctionSegmentCompiler";
import { Stack } from "../../magma/app/compile/Stack";
import { Parameter } from "../../magma/app/compile/define/Parameter";
import { ValueCompiler } from "../../magma/app/ValueCompiler";
import { Symbol } from "../../magma/app/compile/value/Symbol";
import { Symbols } from "../../magma/app/compile/symbol/Symbols";
import { List } from "../../magma/api/collect/list/List";
import { Value } from "../../magma/app/compile/value/Value";
export class FieldCompiler {
	static compileMethod(state: CompileState, input: string): Option<Tuple2<CompileState, string>> {
		let splitter: Splitter = new LocatingSplitter("(", new FirstLocator())/*unknown*/;
		return new SplitComposable<Tuple2<CompileState, string>>(splitter, Composable.toComposable((beforeParams: string, withParams: string) => {
			let strippedBeforeParams = Strings.strip(beforeParams)/*unknown*/;
			return SplitComposable.compileLast(strippedBeforeParams, " ", (_: string, name: string) => {
				if (state.stack().isWithinLast(name)/*unknown*/){
					return FieldCompiler.compileMethodWithBeforeParams(state, new ConstructorHeader(), withParams)/*unknown*/;
				}
				return new None<Tuple2<CompileState, string>>()/*unknown*/;
			}).or(() => {
				if (state.stack().findLastStructureName().filter((anObject: string) => Strings.equalsTo(strippedBeforeParams, anObject)/*unknown*/).isPresent()/*unknown*/){
					return FieldCompiler.compileMethodWithBeforeParams(state, new ConstructorHeader(), withParams)/*unknown*/;
				}
				return new None<Tuple2<CompileState, string>>()/*unknown*/;
			}).or(() => DefiningCompiler.parseDefinition(state, beforeParams).flatMap((tuple: Tuple2<CompileState, Definition>) => FieldCompiler.compileMethodWithBeforeParams(tuple.left(), tuple.right(), withParams)/*unknown*/)/*unknown*/)/*unknown*/;
		})).apply(input)/*unknown*/;
	}
	static compileMethodWithBeforeParams(state: CompileState, header: MethodHeader, withParams: string): Option<Tuple2<CompileState, string>> {
		let splitter: Splitter = new LocatingSplitter(")", new FirstLocator())/*unknown*/;
		return new SplitComposable<Tuple2<CompileState, string>>(splitter, Composable.toComposable((params: string, afterParams: string) => {
			let parametersTuple = DefiningCompiler.parseParameters(state, params)/*unknown*/;
			let parametersState = parametersTuple.left()/*unknown*/;
			let parameters = parametersTuple.right()/*unknown*/;
			let definitions = DefiningCompiler.retainDefinitionsFromParameters(parameters)/*unknown*/;
			let joinedDefinitions = definitions.iter().map((definition: Definition) => definition.generate()/*unknown*/).collect(new Joiner(", ")).orElse("")/*unknown*/;
			if (header.hasAnnotation("Actual")/*unknown*/){
				let headerGenerated = header.removeModifier("static").generateWithAfterName("(" + joinedDefinitions + ")")/*unknown*/;
				return new Some<Tuple2<CompileState, string>>(new Tuple2Impl<CompileState, string>(parametersState, "\n\t" + headerGenerated + ";\n"))/*unknown*/;
			}
			let headerGenerated = header.generateWithAfterName("(" + joinedDefinitions + ")")/*unknown*/;
			return new PrefixComposable<Tuple2<CompileState, string>>("{", (withoutContentStart: string) => new SuffixComposable<Tuple2<CompileState, string>>("}", (withoutContentEnd: string) => {
				let compileState: CompileState = parametersState.enterDepth().enterDepth()/*unknown*/;
				let statementsTuple = FunctionSegmentCompiler.compileFunctionStatements(compileState.mapStack((stack1: Stack) => stack1.defineAll(definitions)/*unknown*/), withoutContentEnd)/*unknown*/;
				return new Some<Tuple2<CompileState, string>>(new Tuple2Impl<CompileState, string>(statementsTuple.left().exitDepth().exitDepth(), "\n\t" + headerGenerated + " {" + statementsTuple.right() + "\n\t}"))/*unknown*/;
			}).apply(Strings.strip(withoutContentStart))/*unknown*/).apply(Strings.strip(afterParams)).or(() => {
				if (Strings.equalsTo(";", Strings.strip(afterParams))/*unknown*/){
					return new Some<Tuple2<CompileState, string>>(new Tuple2Impl<CompileState, string>(parametersState, "\n\t" + headerGenerated + ";"))/*unknown*/;
				}
				return new None<Tuple2<CompileState, string>>()/*unknown*/;
			})/*unknown*/;
		})).apply(withParams)/*unknown*/;
	}
	static compileFieldDefinition(state: CompileState, input: string): Option<Tuple2<CompileState, string>> {
		return new SuffixComposable<Tuple2<CompileState, string>>(";", (withoutEnd: string) => FieldCompiler.getTupleOption(state, withoutEnd).or(() => FieldCompiler.compileEnumValues(state, withoutEnd)/*unknown*/)/*unknown*/).apply(Strings.strip(input))/*unknown*/;
	}
	static getTupleOption(state: CompileState, withoutEnd: string): Option<Tuple2<CompileState, string>> {
		return DefiningCompiler.parseParameter(state, withoutEnd).flatMap((definitionTuple: Tuple2<CompileState, Parameter>) => new Some<Tuple2<CompileState, string>>(new Tuple2Impl<CompileState, string>(definitionTuple.left(), "\n\t" + definitionTuple.right().generate() + ";"))/*unknown*/)/*unknown*/;
	}
	static compileEnumValues(state: CompileState, withoutEnd: string): Option<Tuple2<CompileState, string>> {
		return ValueCompiler.values((state1: CompileState, segment: string) => {
			let stripped = segment.strip()/*unknown*/;
			let state2 = state1.mapStack(stack -  > stack.define(Definition.from(new Symbol("?"), stripped)))/*unknown*/;
			if (Symbols.isSymbol(stripped)/*unknown*/){
				return new Some<Tuple2<CompileState, string>>(new Tuple2Impl<CompileState, string>(state2, "\n\tstatic " + stripped + " = \"" + stripped + "\";"))/*unknown*/;
			}
			return FieldCompiler.compileEnumValue(state, state2, segment)/*unknown*/;
		}).apply(state, withoutEnd).map((tuple: Tuple2<CompileState, List<string>>) => new Tuple2Impl<CompileState, string>(tuple.left(), tuple.right().iter().collect(new Joiner("")).orElse(""))/*unknown*/)/*unknown*/;
	}
	static compileEnumValue(state: CompileState, state1: CompileState, segment: string): Option<Tuple2<CompileState, string>> {
		return ValueCompiler.parseInvokable(state1, segment).flatMap((tuple: Tuple2<CompileState, Value>) => {
			let structureName = state.stack().findLastStructureName().orElse("")/*unknown*/;
			return FieldCompiler.getStringOption(structureName, tuple.right()).map((stringOption: string) => new Tuple2Impl<CompileState, string>(tuple.left(), stringOption)/*unknown*/)/*unknown*/;
		})/*unknown*/;
	}
	static getStringOption(structureName: string, value: Value): Option<string> {
		if (/*value instanceof Invokable invokable*/){
			return new Some<string>("\n\tstatic " + ValueCompiler.generateCaller(invokable.caller()) + ": " + structureName + " = new " + structureName + "(" + ValueCompiler.joinArgs(invokable.args()) + ");")/*unknown*/;
		}
		else {
			return new None<>()/*unknown*/;
		}
	}
}
