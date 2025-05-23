/*[
	JVMList: jvm.api.collect.list, 
	Lists: jvm.api.collect.list, 
	Files: jvm.api.io, 
	Actual: magma.annotate, 
	Namespace: magma.annotate, 
	Collector: magma.api.collect, 
	EmptyHead: magma.api.collect.head, 
	FlatMapHead: magma.api.collect.head, 
	Head: magma.api.collect.head, 
	HeadedIter: magma.api.collect.head, 
	MapHead: magma.api.collect.head, 
	RangeHead: magma.api.collect.head, 
	SingleHead: magma.api.collect.head, 
	Iter: magma.api.collect, 
	Iters: magma.api.collect, 
	Joiner: magma.api.collect, 
	Iterable: magma.api.collect.list, 
	List: magma.api.collect.list, 
	ListCollector: magma.api.collect.list, 
	Sequence: magma.api.collect.list, 
	Console: magma.api.io, 
	IOError: magma.api.io, 
	Path: magma.api.io, 
	None: magma.api.option, 
	Option: magma.api.option, 
	Some: magma.api.option, 
	Err: magma.api.result, 
	Ok: magma.api.result, 
	Result: magma.api.result, 
	Characters: magma.api.text, 
	Strings: magma.api.text, 
	Tuple2: magma.api, 
	Tuple2Impl: magma.api, 
	Application: magma.app, 
	CompileState: magma.app.compile, 
	Composable: magma.app.compile.compose, 
	PrefixComposable: magma.app.compile.compose, 
	SplitComposable: magma.app.compile.compose, 
	SuffixComposable: magma.app.compile.compose, 
	Context: magma.app.compile, 
	ConstructionCaller: magma.app.compile.define, 
	ConstructorHeader: magma.app.compile.define, 
	Definition: magma.app.compile.define, 
	MethodHeader: magma.app.compile.define, 
	Parameter: magma.app.compile.define, 
	Dependency: magma.app.compile, 
	Divider: magma.app.compile.divide, 
	FoldedDivider: magma.app.compile.divide, 
	DivideState: magma.app.compile, 
	DecoratedFolder: magma.app.compile.fold, 
	DelimitedFolder: magma.app.compile.fold, 
	Folder: magma.app.compile.fold, 
	OperatorFolder: magma.app.compile.fold, 
	StatementsFolder: magma.app.compile.fold, 
	TypeSeparatorFolder: magma.app.compile.fold, 
	ValueFolder: magma.app.compile.fold, 
	ImmutableCompileState: magma.app.compile, 
	ImmutableContext: magma.app.compile, 
	ImmutableDivideState: magma.app.compile, 
	ImmutableRegistry: magma.app.compile, 
	ImmutableStack: magma.app.compile, 
	Import: magma.app.compile, 
	FirstLocator: magma.app.compile.locate, 
	LastLocator: magma.app.compile.locate, 
	Locator: magma.app.compile.locate, 
	Merger: magma.app.compile.merge, 
	StatementsMerger: magma.app.compile.merge, 
	ValueMerger: magma.app.compile.merge, 
	Registry: magma.app.compile, 
	OrRule: magma.app.compile.rule, 
	Rule: magma.app.compile.rule, 
	FirstSelector: magma.app.compile.select, 
	LastSelector: magma.app.compile.select, 
	Selector: magma.app.compile.select, 
	FoldingSplitter: magma.app.compile.split, 
	LocatingSplitter: magma.app.compile.split, 
	Splitter: magma.app.compile.split, 
	Stack: magma.app.compile, 
	Whitespace: magma.app.compile.text, 
	FunctionType: magma.app.compile.type, 
	PrimitiveType: magma.app.compile.type, 
	TemplateType: magma.app.compile.type, 
	Type: magma.app.compile.type, 
	VariadicType: magma.app.compile.type, 
	AccessValue: magma.app.compile.value, 
	Argument: magma.app.compile.value, 
	Caller: magma.app.compile.value, 
	Invokable: magma.app.compile.value, 
	Lambda: magma.app.compile.value, 
	Not: magma.app.compile.value, 
	Operation: magma.app.compile.value, 
	Placeholder: magma.app.compile.value, 
	StringValue: magma.app.compile.value, 
	Symbol: magma.app.compile.value, 
	Value: magma.app.compile.value, 
	CompilerUtils: magma.app, 
	DefiningCompiler: magma.app, 
	DefinitionCompiler: magma.app, 
	DivideRule: magma.app, 
	FieldCompiler: magma.app, 
	FunctionSegmentCompiler: magma.app, 
	PathSource: magma.app.io, 
	Source: magma.app.io, 
	Location: magma.app, 
	Main: magma.app, 
	PathSources: magma.app, 
	PathTargets: magma.app, 
	Platform: magma.app, 
	RootCompiler: magma.app, 
	Sources: magma.app, 
	Targets: magma.app, 
	TypeCompiler: magma.app, 
	ValueCompiler: magma.app, 
	WhitespaceCompiler: magma.app
]*/
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
import { List } from "../../magma/api/collect/list/List";
import { Value } from "../../magma/app/compile/value/Value";
class FieldCompiler {
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
				if (state.stack().findLastStructureName().filter((anObject: string) => {
					return Strings.equalsTo(strippedBeforeParams, anObject)/*unknown*/;
				}).isPresent()/*unknown*/){
					return FieldCompiler.compileMethodWithBeforeParams(state, new ConstructorHeader(), withParams)/*unknown*/;
				}
				return new None<Tuple2<CompileState, string>>()/*unknown*/;
			}).or(() => {
				return DefiningCompiler.parseDefinition(state, beforeParams).flatMap((tuple: Tuple2<CompileState, Definition>) => {
					return FieldCompiler.compileMethodWithBeforeParams(tuple.left(), tuple.right(), withParams)/*unknown*/;
				})/*unknown*/;
			})/*unknown*/;
		})).apply(input)/*unknown*/;
	}
	static compileMethodWithBeforeParams(state: CompileState, header: MethodHeader, withParams: string): Option<Tuple2<CompileState, string>> {
		let splitter: Splitter = new LocatingSplitter(")", new FirstLocator())/*unknown*/;
		return new SplitComposable<Tuple2<CompileState, string>>(splitter, Composable.toComposable((params: string, afterParams: string) => {
			let parametersTuple = DefiningCompiler.parseParameters(state, params)/*unknown*/;
			let parametersState = parametersTuple.left()/*unknown*/;
			let parameters = parametersTuple.right()/*unknown*/;
			let definitions = DefiningCompiler.retainDefinitionsFromParameters(parameters)/*unknown*/;
			let joinedDefinitions = definitions.iter().map((definition: Definition) => {
				return definition.generate()/*unknown*/;
			}).collect(new Joiner(", ")).orElse("")/*unknown*/;
			if (header.hasAnnotation("Actual")/*unknown*/){
				let headerGenerated = header.removeModifier("static").generateWithAfterName("(" + joinedDefinitions + ")")/*unknown*/;
				return new Some<Tuple2<CompileState, string>>(new Tuple2Impl<CompileState, string>(parametersState, "\n\t" + headerGenerated + ";\n"))/*unknown*/;
			}
			let headerGenerated = header.generateWithAfterName("(" + joinedDefinitions + ")")/*unknown*/;
			return new PrefixComposable<Tuple2<CompileState, string>>("{", (withoutContentStart: string) => {
				return new SuffixComposable<Tuple2<CompileState, string>>("}", (withoutContentEnd: string) => {
					let compileState: CompileState = parametersState.enterDepth().enterDepth()/*unknown*/;
					let statementsTuple = FunctionSegmentCompiler.compileFunctionStatements(compileState.mapStack((stack1: Stack) => {
						return stack1.defineAll(definitions)/*unknown*/;
					}), withoutContentEnd)/*unknown*/;
					return new Some<Tuple2<CompileState, string>>(new Tuple2Impl<CompileState, string>(statementsTuple.left().exitDepth().exitDepth(), "\n\t" + headerGenerated + " {" + statementsTuple.right() + "\n\t}"))/*unknown*/;
				}).apply(Strings.strip(withoutContentStart))/*unknown*/;
			}).apply(Strings.strip(afterParams)).or(() => {
				if (Strings.equalsTo(";", Strings.strip(afterParams))/*unknown*/){
					return new Some<Tuple2<CompileState, string>>(new Tuple2Impl<CompileState, string>(parametersState, "\n\t" + headerGenerated + ";"))/*unknown*/;
				}
				return new None<Tuple2<CompileState, string>>()/*unknown*/;
			})/*unknown*/;
		})).apply(withParams)/*unknown*/;
	}
	static compileFieldDefinition(state: CompileState, input: string): Option<Tuple2<CompileState, string>> {
		return new SuffixComposable<Tuple2<CompileState, string>>(";", (withoutEnd: string) => {
			return FieldCompiler.getTupleOption(state, withoutEnd).or(() => {
				return FieldCompiler.compileEnumValues(state, withoutEnd)/*unknown*/;
			})/*unknown*/;
		}).apply(Strings.strip(input))/*unknown*/;
	}
	static getTupleOption(state: CompileState, withoutEnd: string): Option<Tuple2<CompileState, string>> {
		return DefiningCompiler.parseParameter(state, withoutEnd).flatMap((definitionTuple: Tuple2<CompileState, Parameter>) => {
			return new Some<Tuple2<CompileState, string>>(new Tuple2Impl<CompileState, string>(definitionTuple.left(), "\n\t" + definitionTuple.right().generate() + ";"))/*unknown*/;
		})/*unknown*/;
	}
	static compileEnumValues(state: CompileState, withoutEnd: string): Option<Tuple2<CompileState, string>> {
		return ValueCompiler.values((state1: CompileState, segment: string) => {
			let stripped = segment.strip()/*unknown*/;
			if (ValueCompiler.isSymbol(stripped)/*unknown*/){
				return new Some<Tuple2<CompileState, string>>(new Tuple2Impl<CompileState, string>(state1, "\n\t static " + stripped + " = \"" + stripped + "\";"))/*unknown*/;
			}
			return FieldCompiler.getTuple2Option(state, state1, segment)/*unknown*/;
		}).apply(state, withoutEnd).map((tuple: Tuple2<CompileState, List<string>>) => {
			return new Tuple2Impl<CompileState, string>(tuple.left(), tuple.right().iter().collect(new Joiner("")).orElse(""))/*unknown*/;
		})/*unknown*/;
	}
	static getTuple2Option(state: CompileState, state1: CompileState, segment: string): Option<Tuple2<CompileState, string>> {
		return ValueCompiler.parseInvokable(state1, segment).flatMap((tuple: Tuple2<CompileState, Value>) => {
			let structureName = state.stack().findLastStructureName().orElse("")/*unknown*/;
			return tuple.right().generateAsEnumValue(structureName).map((stringOption: string) => {
				return new Tuple2Impl<CompileState, string>(tuple.left(), stringOption)/*unknown*/;
			})/*unknown*/;
		})/*unknown*/;
	}
}
