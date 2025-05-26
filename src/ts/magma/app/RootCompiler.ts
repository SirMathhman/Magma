import { CompileState } from "../../magma/app/compile/CompileState";
import { Tuple2 } from "../../magma/api/Tuple2";
import { StatefulOrRule } from "../../magma/app/compile/rule/StatefulOrRule";
import { Lists } from "../../jvm/api/collect/list/Lists";
import { WhitespaceCompiler } from "../../magma/app/WhitespaceCompiler";
import { StatefulRuleAlias } from "../../magma/app/compile/rule/StatefulRuleAlias";
import { SplitComposable } from "../../magma/app/compile/compose/SplitComposable";
import { LocatingSplitter } from "../../magma/app/compile/split/LocatingSplitter";
import { FirstLocator } from "../../magma/app/compile/locate/FirstLocator";
import { Composable } from "../../magma/app/compile/compose/Composable";
import { SuffixRule } from "../../magma/app/compile/rule/SuffixRule";
import { DefiningCompiler } from "../../magma/app/DefiningCompiler";
import { Some } from "../../magma/api/option/Some";
import { Tuple2Impl } from "../../magma/api/Tuple2Impl";
import { Strings } from "../../magma/api/text/Strings";
import { Option } from "../../magma/api/option/Option";
import { List } from "../../magma/api/collect/list/List";
import { TypeCompiler } from "../../magma/app/TypeCompiler";
import { Node } from "../../magma/app/compile/node/Node";
import { None } from "../../magma/api/option/None";
import { Splitter } from "../../magma/app/compile/split/Splitter";
import { ValueCompiler } from "../../magma/app/ValueCompiler";
import { Iterable } from "../../magma/api/collect/list/Iterable";
import { Definition } from "../../magma/app/compile/define/Definition";
import { SymbolRule } from "../../magma/app/compile/rule/SymbolRule";
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
		return StatefulOrRule.compileOrPlaceholder(state, input, Lists.of(WhitespaceCompiler.compileWhitespace, RootCompiler.compileNamespaced, RootCompiler.createStructureRule("class ", "class "), RootCompiler.createStructureRule("interface ", "interface "), RootCompiler.createStructureRule("record ", "class "), RootCompiler.createStructureRule("enum ", "class ")))/*unknown*/;
	}
	static createStructureRule(sourceInfix: string, targetInfix: string): StatefulRuleAlias<string> {
		return (state: CompileState, input1: string) => {
			return new SplitComposable<Tuple2<CompileState, string>>(new LocatingSplitter(sourceInfix, new FirstLocator()), Composable.toComposable((beforeInfix: string, afterInfix: string) => {
				return new SplitComposable<Tuple2<CompileState, string>>(new LocatingSplitter("{", new FirstLocator()), Composable.toComposable((beforeContent: string, withEnd: string) => {
					return new SuffixRule<Tuple2<CompileState, string>>("}", (inputContent: string) => {
						return SplitComposable.compileLast(beforeInfix, "\n", (s: string, s2: string) => {
							let annotations = DefiningCompiler.parseAnnotations(s)/*unknown*/;
							if (annotations.contains("Actual")/*unknown*/){
								return new Some<Tuple2<CompileState, string>>(new Tuple2Impl<CompileState, string>(state, ""))/*unknown*/;
							}
							return RootCompiler.compileStructureWithImplementing(state, annotations, DefiningCompiler.parseModifiers(s2), targetInfix, beforeContent, inputContent)/*unknown*/;
						}).or(() => {
							let modifiers = DefiningCompiler.parseModifiers(beforeContent)/*unknown*/;
							return RootCompiler.compileStructureWithImplementing(state, Lists.empty(), modifiers, targetInfix, beforeContent, inputContent)/*unknown*/;
						})/*unknown*/;
					}).lex(Strings.strip(withEnd))/*unknown*/;
				})).apply(afterInfix)/*unknown*/;
			})).apply(input1)/*unknown*/;
		}/*unknown*/;
	}
	static compileStructureWithImplementing(state: CompileState, annotations: List<string>, modifiers: List<string>, targetInfix: string, beforeContent: string, content: string): Option<Tuple2<CompileState, string>> {
		return SplitComposable.compileLast(beforeContent, " implements ", (s: string, s2: string) => {
			return TypeCompiler.lexType(s2).flatMap((content1: Node) => TypeCompiler.parseType(state, content1)/*unknown*/).flatMap((implementingTuple: Tuple2<CompileState, Node>) => {
				return RootCompiler.compileStructureWithExtends(implementingTuple.left(), annotations, modifiers, targetInfix, s, new Some<Node>(implementingTuple.right()), content)/*unknown*/;
			})/*unknown*/;
		}).or(() => {
			return RootCompiler.compileStructureWithExtends(state, annotations, modifiers, targetInfix, beforeContent, new None<Node>(), content)/*unknown*/;
		})/*unknown*/;
	}
	static compileStructureWithExtends(state: CompileState, annotations: List<string>, modifiers: List<string>, targetInfix: string, beforeContent: string, maybeImplementing: Option<Node>, inputContent: string): Option<Tuple2<CompileState, string>> {
		let splitter: Splitter = new LocatingSplitter(" extends ", new FirstLocator())/*unknown*/;
		return new SplitComposable<Tuple2<CompileState, string>>(splitter, Composable.toComposable((beforeExtends: string, afterExtends: string) => {
			return ValueCompiler.values((inner0: CompileState, inner1: string) => {
				return TypeCompiler.lexType(inner1).flatMap((content: Node) => TypeCompiler.parseType(inner0, content)/*unknown*/)/*unknown*/;
			}).apply(state, afterExtends).flatMap((compileStateListTuple2: Tuple2<CompileState, List<Node>>) => {
				return RootCompiler.compileStructureWithParameters(compileStateListTuple2.left(), annotations, modifiers, targetInfix, beforeExtends, compileStateListTuple2.right(), maybeImplementing, inputContent)/*unknown*/;
			})/*unknown*/;
		})).apply(beforeContent).or(() => {
			return RootCompiler.compileStructureWithParameters(state, annotations, modifiers, targetInfix, beforeContent, Lists.empty(), maybeImplementing, inputContent)/*unknown*/;
		})/*unknown*/;
	}
	static compileStructureWithParameters(state: CompileState, annotations: List<string>, modifiers: List<string>, targetInfix: string, beforeContent: string, maybeSuperNode: Iterable<Node>, maybeImplementing: Option<Node>, inputContent: string): Option<Tuple2<CompileState, string>> {
		let splitter1: Splitter = new LocatingSplitter("(", new FirstLocator())/*unknown*/;
		return new SplitComposable<Tuple2<CompileState, string>>(splitter1, Composable.toComposable((rawName: string, withParameters: string) => {
			let splitter: Splitter = new LocatingSplitter(")", new FirstLocator())/*unknown*/;
			return new SplitComposable<Tuple2<CompileState, string>>(splitter, Composable.toComposable((parametersString: string, _: string) => {
				let name = Strings.strip(rawName)/*unknown*/;
				let parametersTuple = DefiningCompiler.parseParameters(state, parametersString)/*unknown*/;
				let parameters = DefiningCompiler.retainDefinitionsFromParameters(parametersTuple.right())/*unknown*/;
				return RootCompiler.compileStructureWithNodeParams(parametersTuple.left(), targetInfix, inputContent, name, parameters, maybeImplementing, annotations, modifiers, maybeSuperNode)/*unknown*/;
			})).apply(withParameters)/*unknown*/;
		})).apply(beforeContent).or(() => {
			return RootCompiler.compileStructureWithNodeParams(state, targetInfix, inputContent, beforeContent, Lists.empty(), maybeImplementing, annotations, modifiers, maybeSuperNode)/*unknown*/;
		})/*unknown*/;
	}
	static compileStructureWithNodeParams(state: CompileState, infix: string, content: string, beforeParams: string, parameters: Iterable<Definition>, maybeImplementing: Option<Node>, annotations: List<string>, modifiers: List<string>, maybeSuperNode: Iterable<Node>): Option<Tuple2<CompileState, string>> {
		return new SuffixRule<Tuple2<CompileState, string>>(">", (withoutNodeParamEnd: string) => {
			let splitter: Splitter = new LocatingSplitter("<", new FirstLocator())/*unknown*/;
			return new SplitComposable<Tuple2<CompileState, string>>(splitter, Composable.toComposable((name: string, typeParamsString: string) => {
				let typeParams = DefiningCompiler.divideNodes(typeParamsString)/*unknown*/;
				return RootCompiler.assembleStructure(state, annotations, modifiers, infix, name, typeParams, parameters, maybeImplementing, content, maybeSuperNode)/*unknown*/;
			})).apply(withoutNodeParamEnd)/*unknown*/;
		}).lex(Strings.strip(beforeParams)).or(() => {
			return RootCompiler.assembleStructure(state, annotations, modifiers, infix, beforeParams, Lists.empty(), parameters, maybeImplementing, content, maybeSuperNode)/*unknown*/;
		})/*unknown*/;
	}
	static assembleStructure(state: CompileState, annotations: List<string>, oldModifiers: List<string>, infix: string, rawName: string, typeParams: Iterable<string>, parameters: Iterable<Definition>, maybeImplementing: Option<Node>, content: string, maybeSuperNode: Iterable<Node>): Option<Tuple2<CompileState, string>> {
		let name = Strings.strip(rawName)/*unknown*/;
		if (!SymbolRule/*unknown*/.isSymbol(name)/*unknown*/){
			return new None<Tuple2<CompileState, string>>()/*unknown*/;
		}
		let outputContentTuple = FunctionSegmentCompiler.compileStatements(state.mapStack((stack: Stack) => {
			return stack.pushStructureName(name)/*unknown*/;
		}), content, RootCompiler.compileClassSegment)/*unknown*/;
		let outputContentState = outputContentTuple.left().mapStack((stack1: Stack) => {
			return stack1.popStructureName()/*unknown*/;
		})/*unknown*/;
		let outputContent = outputContentTuple.right()/*unknown*/;
		let constructorString = RootCompiler.generateConstructorFromRecordParameters(parameters)/*unknown*/;
		let joinedNodeParams = RootCompiler.joinNodeParams(typeParams)/*unknown*/;
		let implementingString = RootCompiler.generateImplementing(maybeImplementing)/*unknown*/;
		let newModifiers = RootCompiler.modifyModifiers0(oldModifiers)/*unknown*/;
		let joinedModifiers = newModifiers.iter().map((value: string) => {
			return value + " "/*unknown*/;
		}).collect(Joiner.empty()).orElse("")/*unknown*/;
		if (outputContentState.context().hasPlatform(Platform.PlantUML)/*unknown*/){
			let joinedImplementing = maybeImplementing.map((type: Node) => {
				return TypeCompiler.generateSimple(type)/*unknown*/;
			}).map((generated: string) => {
				return name + " <|.. " + generated + "\n"/*unknown*/;
			}).orElse("")/*unknown*/;
			let joinedSuperNodes = maybeSuperNode.iter().map((type: Node) => {
				return TypeCompiler.generateSimple(type)/*unknown*/;
			}).map((generated: string) => {
				return name + " <|-- " + generated + "\n"/*unknown*/;
			}).collect(new Joiner("")).orElse("")/*unknown*/;
			let generated = infix + name + joinedNodeParams + " {\n}\n" + joinedSuperNodes + joinedImplementing/*unknown*/;
			return new Some<Tuple2<CompileState, string>>(new Tuple2Impl<CompileState, string>(outputContentState.mapRegistry((registry: Registry) => {
				return registry.append(generated)/*unknown*/;
			}), ""))/*unknown*/;
		}
		if (annotations.contains("Namespace")/*unknown*/){
			let actualInfix: string = "interface "/*unknown*/;
			let newName: string = name + "Instance"/*unknown*/;
			let generated = joinedModifiers + actualInfix + newName + joinedNodeParams + implementingString + " {" + DefiningCompiler.joinParameters(parameters) + constructorString + outputContent + "\n}\n"/*unknown*/;
			let compileState: CompileState = outputContentState.mapRegistry((registry: Registry) => {
				return registry.append(generated)/*unknown*/;
			})/*unknown*/;
			return new Some<Tuple2<CompileState, string>>(new Tuple2Impl<CompileState, string>(compileState.mapRegistry((registry1: Registry) => {
				return registry1.append("export declare const " + name + ": " + newName + ";\n")/*unknown*/;
			}), ""))/*unknown*/;
		}
		else {
			let extendsString = RootCompiler.joinExtends(maybeSuperNode)/*unknown*/;
			let generated = joinedModifiers + infix + name + joinedNodeParams + extendsString + implementingString + " {" + DefiningCompiler.joinParameters(parameters) + constructorString + outputContent + "\n}\n"/*unknown*/;
			return new Some<Tuple2<CompileState, string>>(new Tuple2Impl<CompileState, string>(outputContentState.mapRegistry((registry: Registry) => {
				return registry.append(generated)/*unknown*/;
			}), ""))/*unknown*/;
		}
	}
	static joinExtends(maybeSuperNode: Iterable<Node>): string {
		return maybeSuperNode.iter().map((type: Node) => {
			return TypeCompiler.generateType(type)/*unknown*/;
		}).collect(new Joiner(", ")).map((inner: string) => {
			return " extends " + inner/*unknown*/;
		}).orElse("")/*unknown*/;
	}
	static modifyModifiers0(oldModifiers: List<string>): Iterable<string> {
		if (oldModifiers.contains("public")/*unknown*/){
			return Lists.of("export")/*unknown*/;
		}
		return Lists.empty()/*unknown*/;
	}
	static generateImplementing(maybeImplementing: Option<Node>): string {
		return maybeImplementing.map((type: Node) => {
			return TypeCompiler.generateType(type)/*unknown*/;
		}).map((inner: string) => {
			return " implements " + inner/*unknown*/;
		}).orElse("")/*unknown*/;
	}
	static joinNodeParams(typeParams: Iterable<string>): string {
		return typeParams.iter().collect(new Joiner(", ")).map((inner: string) => {
			return "<" + inner + ">"/*unknown*/;
		}).orElse("")/*unknown*/;
	}
	static generateConstructorFromRecordParameters(parameters: Iterable<Definition>): string {
		return parameters.iter().map((definition: Definition) => {
			return DefiningCompiler.generateParameter(definition)/*unknown*/;
		}).collect(new Joiner(", ")).map((generatedParameters: string) => {
			return RootCompiler.generateConstructorWithParameterString(parameters, generatedParameters)/*unknown*/;
		}).orElse("")/*unknown*/;
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
		return StatefulOrRule.compileOrPlaceholder(state1, input1, Lists.of(WhitespaceCompiler.compileWhitespace, RootCompiler.createStructureRule("class ", "class "), RootCompiler.createStructureRule("interface ", "interface "), RootCompiler.createStructureRule("record ", "class "), RootCompiler.createStructureRule("enum ", "class "), FieldCompiler.compileMethod, FieldCompiler.compileFieldDefinition, FieldCompiler.compileEnumNodes))/*unknown*/;
	}
	static compileRoot(state: CompileState, input: string, location: Location): Tuple2<CompileState, string> {
		return FunctionSegmentCompiler.compileStatements(state.mapContext((context2: Context) => {
			return context2.withLocation(location)/*unknown*/;
		}), input, RootCompiler.compileRootSegment)/*unknown*/;
	}
}
