import { CompileState } from "../../magma/app/compile/CompileState";
import { Tuple2 } from "../../magma/api/Tuple2";
import { OrRule } from "../../magma/app/compile/rule/OrRule";
import { Lists } from "../../jvm/api/collect/list/Lists";
import { WhitespaceCompiler } from "../../magma/app/WhitespaceCompiler";
import { Rule } from "../../magma/app/compile/rule/Rule";
import { SplitComposable } from "../../magma/app/compile/compose/SplitComposable";
import { LocatingSplitter } from "../../magma/app/compile/split/LocatingSplitter";
import { FirstLocator } from "../../magma/app/compile/locate/FirstLocator";
import { Composable } from "../../magma/app/compile/compose/Composable";
import { SuffixComposable } from "../../magma/app/compile/compose/SuffixComposable";
import { DefiningCompiler } from "../../magma/app/DefiningCompiler";
import { Some } from "../../magma/api/option/Some";
import { Tuple2Impl } from "../../magma/api/Tuple2Impl";
import { Strings } from "../../magma/api/text/Strings";
import { Option } from "../../magma/api/option/Option";
import { List } from "../../magma/api/collect/list/List";
import { TypeCompiler } from "../../magma/app/TypeCompiler";
import { Type } from "../../magma/app/compile/type/Type";
import { None } from "../../magma/api/option/None";
import { Splitter } from "../../magma/app/compile/split/Splitter";
import { ValueCompiler } from "../../magma/app/ValueCompiler";
import { Iterable } from "../../magma/api/collect/list/Iterable";
import { Definition } from "../../magma/app/compile/define/Definition";
import { Symbols } from "../../magma/app/compile/symbol/Symbols";
import { FunctionSegmentCompiler } from "../../magma/app/FunctionSegmentCompiler";
import { Stack } from "../../magma/app/compile/Stack";
import { Joiner } from "../../magma/api/collect/Joiner";
import { Platform } from "../../magma/app/Platform";
import { Registry } from "../../magma/app/compile/Registry";
import { FieldCompiler } from "../../magma/app/FieldCompiler";
import { Location } from "../../magma/app/Location";
import { Context } from "../../magma/app/compile/Context";
export class RootCompiler {
	static compileRootSegment(state: CompileState, input: string): Tuple2<CompileState, string> {
		return OrRule.compileOrPlaceholder(state, input, Lists.of(WhitespaceCompiler.compileWhitespace, RootCompiler.compileNamespaced, RootCompiler.createStructureRule("class ", "class "), RootCompiler.createStructureRule("interface ", "interface "), RootCompiler.createStructureRule("record ", "class "), RootCompiler.createStructureRule("enum ", "class ")))/*unknown*/;
	}
	static createStructureRule(sourceInfix: string, targetInfix: string): Rule<string> {
		return (state: CompileState, input1: string) => new SplitComposable<Tuple2<CompileState, string>>(new LocatingSplitter(sourceInfix, new FirstLocator()), Composable.toComposable((beforeInfix: string, afterInfix: string) => new SplitComposable<Tuple2<CompileState, string>>(new LocatingSplitter("{", new FirstLocator()), Composable.toComposable((beforeContent: string, withEnd: string) => new SuffixComposable<Tuple2<CompileState, string>>("}", (inputContent: string) => SplitComposable.compileLast(beforeInfix, "\n", (s: string, s2: string) => {
			let annotations = DefiningCompiler.parseAnnotations(s)/*unknown*/;
			if (annotations.contains("Actual")/*unknown*/){
				return new Some<Tuple2<CompileState, string>>(new Tuple2Impl<CompileState, string>(state, ""))/*unknown*/;
			}
			return RootCompiler.compileStructureWithImplementing(state, annotations, DefiningCompiler.parseModifiers(s2), targetInfix, beforeContent, inputContent, sourceInfix)/*unknown*/;
		}).or(() => {
			let modifiers = DefiningCompiler.parseModifiers(beforeContent)/*unknown*/;
			return RootCompiler.compileStructureWithImplementing(state, Lists.empty(), modifiers, targetInfix, beforeContent, inputContent, sourceInfix)/*unknown*/;
		})/*unknown*/).apply(Strings.strip(withEnd))/*unknown*/)).apply(afterInfix)/*unknown*/)).apply(input1)/*unknown*//*unknown*/;
	}
	static compileStructureWithImplementing(state: CompileState, annotations: List<string>, modifiers: List<string>, targetInfix: string, beforeContent: string, content: string, sourceInfix: string): Option<Tuple2<CompileState, string>> {
		return SplitComposable.compileLast(beforeContent, " implements ", (s: string, s2: string) => TypeCompiler.createTypeRule().apply(state, s2).flatMap((implementingTuple: Tuple2<CompileState, Type>) => RootCompiler.compileStructureWithExtends(implementingTuple.left(), annotations, targetInfix, s, new Some<Type>(implementingTuple.right()), content, sourceInfix)/*unknown*/)/*unknown*/).or(() => RootCompiler.compileStructureWithExtends(state, annotations, targetInfix, beforeContent, new None<Type>(), content, sourceInfix)/*unknown*/)/*unknown*/;
	}
	static compileStructureWithExtends(state: CompileState, annotations: List<string>, targetInfix: string, beforeContent: string, maybeImplementing: Option<Type>, inputContent: string, sourceInfix: string): Option<Tuple2<CompileState, string>> {
		let splitter: Splitter = new LocatingSplitter(" extends ", new FirstLocator())/*unknown*/;
		return new SplitComposable<Tuple2<CompileState, string>>(splitter, Composable.toComposable((beforeExtends: string, afterExtends: string) => ValueCompiler.values((inner0: CompileState, inner1: string) => TypeCompiler.createTypeRule().apply(inner0, inner1)/*unknown*/).apply(state, afterExtends).flatMap((compileStateListTuple2: Tuple2<CompileState, List<Type>>) => RootCompiler.compileStructureWithParameters(compileStateListTuple2.left(), annotations, targetInfix, beforeExtends, compileStateListTuple2.right(), maybeImplementing, inputContent, sourceInfix)/*unknown*/)/*unknown*/)).apply(beforeContent).or(() => RootCompiler.compileStructureWithParameters(state, annotations, targetInfix, beforeContent, Lists.empty(), maybeImplementing, inputContent, sourceInfix)/*unknown*/)/*unknown*/;
	}
	static compileStructureWithParameters(state: CompileState, annotations: List<string>, targetInfix: string, beforeContent: string, maybeSuperType: Iterable<Type>, maybeImplementing: Option<Type>, inputContent: string, sourceInfix: string): Option<Tuple2<CompileState, string>> {
		let splitter1: Splitter = new LocatingSplitter("(", new FirstLocator())/*unknown*/;
		return new SplitComposable<Tuple2<CompileState, string>>(splitter1, Composable.toComposable((rawName: string, withParameters: string) => {
			let splitter: Splitter = new LocatingSplitter(")", new FirstLocator())/*unknown*/;
			return new SplitComposable<Tuple2<CompileState, string>>(splitter, Composable.toComposable((parametersString: string, _: string) => {
				let name = Strings.strip(rawName)/*unknown*/;
				let parametersTuple = DefiningCompiler.parseParameters(state, parametersString)/*unknown*/;
				let parameters = DefiningCompiler.retainDefinitionsFromParameters(parametersTuple.right())/*unknown*/;
				return RootCompiler.compileStructureWithTypeParams(parametersTuple.left(), targetInfix, inputContent, name, parameters, maybeImplementing, annotations, maybeSuperType, sourceInfix)/*unknown*/;
			})).apply(withParameters)/*unknown*/;
		})).apply(beforeContent).or(() => RootCompiler.compileStructureWithTypeParams(state, targetInfix, inputContent, beforeContent, Lists.empty(), maybeImplementing, annotations, maybeSuperType, sourceInfix)/*unknown*/)/*unknown*/;
	}
	static compileStructureWithTypeParams(state: CompileState, infix: string, content: string, beforeParams: string, parameters: Iterable<Definition>, maybeImplementing: Option<Type>, annotations: List<string>, maybeSuperType: Iterable<Type>, sourceInfix: string): Option<Tuple2<CompileState, string>> {
		return new SuffixComposable<Tuple2<CompileState, string>>(">", (withoutTypeParamEnd: string) => {
			let splitter: Splitter = new LocatingSplitter("<", new FirstLocator())/*unknown*/;
			return new SplitComposable<Tuple2<CompileState, string>>(splitter, Composable.toComposable((name: string, typeParamsString: string) => {
				let typeParams = DefiningCompiler.divideValues(typeParamsString)/*unknown*/;
				return RootCompiler.assembleStructure(state, annotations, infix, name, typeParams, parameters, maybeImplementing, content, maybeSuperType, sourceInfix)/*unknown*/;
			})).apply(withoutTypeParamEnd)/*unknown*/;
		}).apply(Strings.strip(beforeParams)).or(() => RootCompiler.assembleStructure(state, annotations, infix, beforeParams, Lists.empty(), parameters, maybeImplementing, content, maybeSuperType, sourceInfix)/*unknown*/)/*unknown*/;
	}
	static assembleStructure(state: CompileState, annotations: List<string>, targetInfix: string, rawName: string, typeParams: Iterable<string>, parameters: Iterable<Definition>, maybeImplementing: Option<Type>, content: string, maybeSuperType: Iterable<Type>, sourceInfix: string): Option<Tuple2<CompileState, string>> {
		let name = Strings.strip(rawName)/*unknown*/;
		if (!!Symbols/*unknown*/.isSymbol(name)/*unknown*/){
			return new None<Tuple2<CompileState, string>>()/*unknown*/;
		}
		let outputContentTuple = FunctionSegmentCompiler.compileStatements(state.mapStack((stack: Stack) => stack.pushStructureName(name)/*unknown*/), content, RootCompiler.compileClassSegment)/*unknown*/;
		let outputContentState = outputContentTuple.left().mapStack((stack1: Stack) => stack1.popStructureName()/*unknown*/)/*unknown*/;
		let outputContent = outputContentTuple.right()/*unknown*/;
		let constructorString = RootCompiler.generateConstructorFromRecordParameters(parameters)/*unknown*/;
		let joinedTypeParams = RootCompiler.joinTypeParams(typeParams)/*unknown*/;
		let implementingString = RootCompiler.generateImplementing(maybeImplementing)/*unknown*/;
		let newModifiers = Lists.of("export")/*unknown*/;
		let joinedModifiers = newModifiers.iter().map((value: string) => value + " "/*unknown*/).collect(Joiner.empty()).orElse("")/*unknown*/;
		if (outputContentState.context().hasPlatform(Platform.PlantUML)/*unknown*/){
			let joinedImplementing = maybeImplementing.map((type: Type) => type.generateSimple()/*unknown*/).map((generated: string) => name + " <|.. " + generated + "\n"/*unknown*/).orElse("")/*unknown*/;
			let joinedSuperTypes = maybeSuperType.iter().map((type: Type) => type.generateSimple()/*unknown*/).map((generated: string) => name + " <|-- " + generated + "\n"/*unknown*/).collect(new Joiner("")).orElse("")/*unknown*/;
			let generated = targetInfix + name + joinedTypeParams + " {\n}\n" + joinedSuperTypes + joinedImplementing/*unknown*/;
			return new Some<Tuple2<CompileState, string>>(new Tuple2Impl<CompileState, string>(outputContentState.mapRegistry((registry: Registry) => registry.append(generated)/*unknown*/), ""))/*unknown*/;
		}
		if (annotations.contains("Namespace")/*unknown*/){
			let actualInfix: string = "interface "/*string*/;
			let newName: string = name + "Instance"/*unknown*/;
			let generated = joinedModifiers + actualInfix + newName + joinedTypeParams + implementingString + " {" + RootCompiler.joinParameters(parameters) + constructorString + outputContent + "\n}\n"/*unknown*/;
			let compileState = outputContentState.mapRegistry((registry: Registry) => registry.append(generated)/*unknown*/)/*unknown*/;
			return new Some<Tuple2<CompileState, string>>(new Tuple2Impl<CompileState, string>(compileState.mapRegistry((registry1: Registry) => registry1.append("export declare const " + name + ": " + newName + ";\n")/*unknown*/), ""))/*unknown*/;
		}
		let maybeValuesMethod = RootCompiler.generateValuesMethod(sourceInfix, outputContentState, name)/*unknown*/;
		let extendsString = RootCompiler.joinExtends(maybeSuperType)/*unknown*/;
		let generated = joinedModifiers + targetInfix + name + joinedTypeParams + extendsString + implementingString + " {" + RootCompiler.joinParameters(parameters) + constructorString + outputContent + maybeValuesMethod + "\n}\n"/*unknown*/;
		return new Some<Tuple2<CompileState, string>>(new Tuple2Impl<CompileState, string>(outputContentState.mapRegistry((registry: Registry) => registry.append(generated)/*unknown*/), ""))/*unknown*/;
	}
	static generateValuesMethod(sourceInfix: string, outputContentState: CompileState, name: string): string {
		if (!!"enum "/*string*/.equals(sourceInfix)/*unknown*/){
			return ""/*string*/;
		}
		let joined = RootCompiler.mergeEnumValues(outputContentState, name)/*unknown*/;
		return "\n\tstatic values(): " + name + "[] {\n\t\treturn [" + joined + "];\n\t}"/*unknown*/;
	}
	static mergeEnumValues(outputContentState: CompileState, name: string): string {
		return outputContentState.stack().findLastDefinitions().iter().map((definition: Definition) => name + "." + definition.name()/*unknown*/).collect(new Joiner(", ")).orElse("")/*unknown*/;
	}
	static joinExtends(maybeSuperType: Iterable<Type>): string {
		return maybeSuperType.iter().map((type: Type) => TypeCompiler.generateType(type)/*unknown*/).collect(new Joiner(", ")).map((inner: string) => " extends " + inner/*unknown*/).orElse("")/*unknown*/;
	}
	static generateImplementing(maybeImplementing: Option<Type>): string {
		return maybeImplementing.map((type: Type) => TypeCompiler.generateType(type)/*unknown*/).map((inner: string) => " implements " + inner/*unknown*/).orElse("")/*unknown*/;
	}
	static joinTypeParams(typeParams: Iterable<string>): string {
		return typeParams.iter().collect(new Joiner(", ")).map((inner: string) => "<" + inner + ">"/*unknown*/).orElse("")/*unknown*/;
	}
	static generateConstructorFromRecordParameters(parameters: Iterable<Definition>): string {
		return parameters.iter().map((definition: Definition) => definition.generate()/*unknown*/).collect(new Joiner(", ")).map((generatedParameters: string) => RootCompiler.generateConstructorWithParameterString(parameters, generatedParameters)/*unknown*/).orElse("")/*unknown*/;
	}
	static generateConstructorWithParameterString(parameters: Iterable<Definition>, parametersString: string): string {
		let constructorAssignments = RootCompiler.generateConstructorAssignments(parameters)/*unknown*/;
		return "\n\tconstructor (" + parametersString + ") {" + constructorAssignments + "\n\t}"/*unknown*/;
	}
	static generateConstructorAssignments(parameters: Iterable<Definition>): string {
		return parameters.iter().map((definition: Definition) => "\n\t\tthis." + definition.name() + " = " + definition.name() + ";"/*unknown*/).collect(Joiner.empty()).orElse("")/*unknown*/;
	}
	static compileNamespaced(state: CompileState, input: string): Option<Tuple2<CompileState, string>> {
		let stripped = Strings.strip(input)/*unknown*/;
		if (stripped.startsWith("package ") || stripped.startsWith("import ")/*unknown*/){
			return new Some<Tuple2<CompileState, string>>(new Tuple2Impl<CompileState, string>(state, ""))/*unknown*/;
		}
		return new None<Tuple2<CompileState, string>>()/*unknown*/;
	}
	static compileClassSegment(state1: CompileState, input1: string): Tuple2<CompileState, string> {
		return OrRule.compileOrPlaceholder(state1, input1, Lists.of(WhitespaceCompiler.compileWhitespace, RootCompiler.createStructureRule("class ", "class "), RootCompiler.createStructureRule("interface ", "interface "), RootCompiler.createStructureRule("record ", "class "), RootCompiler.createStructureRule("enum ", "class "), FieldCompiler.compileMethod, FieldCompiler.compileFieldDefinition, FieldCompiler.compileEnumValues))/*unknown*/;
	}
	static compileRoot(state: CompileState, input: string, location: Location): Tuple2<CompileState, string> {
		return FunctionSegmentCompiler.compileStatements(state.mapContext((context2: Context) => context2.withLocation(location)/*unknown*/), input, RootCompiler.compileRootSegment)/*unknown*/;
	}
	static joinParameters(parameters: Iterable<Definition>): string {
		return parameters.iter().map((definition: Definition) => definition.generate()/*unknown*/).map((generated: string) => "\n\t" + generated + ";"/*unknown*/).collect(Joiner.empty()).orElse("")/*unknown*/;
	}
}
