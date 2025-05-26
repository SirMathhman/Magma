import { CompileState } from "../../magma/app/compile/CompileState";
import { Tuple2 } from "../../magma/api/Tuple2";
import { Option } from "../../magma/api/option/Option";
import { Node } from "../../magma/app/compile/node/Node";
import { Tuple2Impl } from "../../magma/api/Tuple2Impl";
import { OrRule } from "../../magma/app/compile/rule/OrRule";
import { Lists } from "../../jvm/api/collect/list/Lists";
import { Strings } from "../../magma/api/text/Strings";
import { SuffixComposable } from "../../magma/app/compile/compose/SuffixComposable";
import { Some } from "../../magma/api/option/Some";
import { MapNode } from "../../magma/app/compile/node/MapNode";
import { ValueCompiler } from "../../magma/app/ValueCompiler";
import { None } from "../../magma/api/option/None";
import { LocatingSplitter } from "../../magma/app/compile/split/LocatingSplitter";
import { FirstLocator } from "../../magma/app/compile/locate/FirstLocator";
import { Splitter } from "../../magma/app/compile/split/Splitter";
import { SplitComposable } from "../../magma/app/compile/compose/SplitComposable";
import { Composable } from "../../magma/app/compile/compose/Composable";
import { WhitespaceCompiler } from "../../magma/app/WhitespaceCompiler";
import { List } from "../../magma/api/collect/list/List";
import { Location } from "../../magma/app/Location";
import { Import } from "../../magma/app/compile/Import";
import { Registry } from "../../magma/app/compile/Registry";
import { Source } from "../../magma/app/io/Source";
import { Platform } from "../../magma/app/Platform";
import { Dependency } from "../../magma/app/compile/Dependency";
import { Placeholders } from "../../magma/app/compile/define/Placeholders";
import { Joiner } from "../../magma/api/collect/Joiner";
export class TypeCompiler {/*public static final Node Boolean = new MapNode("boolean").withString("value", "boolean");*//*
    public static final Node Number = new MapNode("number").withString("value", "number");*//*
    public static final Node String = new MapNode("string").withString("value", "string");*//*
    public static final Node Unknown = new MapNode("unknown").withString("value", "unknown");*//*
    public static final Node Var = new MapNode("var").withString("value", "var");*//*
    public static final Node Void = new MapNode("void").withString("value", "void");*//*
    public static final List<String> variants = Lists.of("boolean", "number", "string", "unknown", "var", "void");*/
	static compileType(state: CompileState, type: string): Option<Tuple2<CompileState, string>> {
		return TypeCompiler.parseType(state, type).map((tuple: Tuple2<CompileState, Node>) => {
			return new Tuple2Impl<CompileState, string>(tuple.left(), TypeCompiler.generateType(tuple.right()))/*unknown*/;
		})/*unknown*/;
	}
	static parseType(state: CompileState, type: string): Option<Tuple2<CompileState, Node>> {
		return new OrRule<Node>(Lists.of(TypeCompiler.parseVarArgs, TypeCompiler.parseGeneric, TypeCompiler.parsePrimitive, TypeCompiler.parseSymbolNode)).apply(state, type)/*unknown*/;
	}
	static parseVarArgs(state: CompileState, input: string): Option<Tuple2<CompileState, Node>> {
		let stripped = Strings.strip(input)/*unknown*/;
		return new SuffixComposable<Tuple2<CompileState, Node>>("...", (s: string) => {
			let child = TypeCompiler.parseNodeOrPlaceholder(state, s)/*unknown*/;
			let type: Node = child.right()/*unknown*/;
			return new Some<Tuple2<CompileState, Node>>(new Tuple2Impl<CompileState, Node>(child.left(), new MapNode("variadic").withNode("child", type)))/*unknown*/;
		}).apply(stripped)/*unknown*/;
	}
	static parseSymbolNode(state: CompileState, input: string): Option<Tuple2<CompileState, Node>> {
		let stripped = Strings.strip(input)/*unknown*/;
		if (ValueCompiler.isSymbol(stripped)/*unknown*/){
			let resolved: CompileState = TypeCompiler.addResolvedImportFromCache0(state, stripped)/*unknown*/;
			return new Some<Tuple2<CompileState, Node>>(new Tuple2Impl<CompileState, Node>(resolved, new MapNode("symbol").withString("value", stripped)))/*unknown*/;
		}
		return new None<Tuple2<CompileState, Node>>()/*unknown*/;
	}
	static parsePrimitive(state: CompileState, input: string): Option<Tuple2<CompileState, Node>> {
		return TypeCompiler.findPrimitiveNode(Strings.strip(input)).map((result: Node) => {
			return new Tuple2Impl<CompileState, Node>(state, result)/*unknown*/;
		})/*unknown*/;
	}
	static findPrimitiveNode(input: string): Option<Node> {
		let stripped = Strings.strip(input)/*unknown*/;
		if (Strings.equalsTo("char", stripped) || Strings.equalsTo("Character", stripped) || Strings.equalsTo("String", stripped)/*unknown*/){
			return new Some<Node>(TypeCompiler.String)/*unknown*/;
		}
		if (Strings.equalsTo("int", stripped) || Strings.equalsTo("Integer", stripped)/*unknown*/){
			return new Some<Node>(TypeCompiler.Number)/*unknown*/;
		}
		if (Strings.equalsTo("boolean", stripped) || Strings.equalsTo("Boolean", stripped)/*unknown*/){
			return new Some<Node>(TypeCompiler.Boolean)/*unknown*/;
		}
		if (Strings.equalsTo("var", stripped)/*unknown*/){
			return new Some<Node>(TypeCompiler.Var)/*unknown*/;
		}
		if (Strings.equalsTo("void", stripped)/*unknown*/){
			return new Some<Node>(TypeCompiler.Void)/*unknown*/;
		}
		return new None<Node>()/*unknown*/;
	}
	static parseGeneric(state: CompileState, input: string): Option<Tuple2<CompileState, Node>> {
		return new SuffixComposable<Tuple2<CompileState, Node>>(">", (withoutEnd: string) => {
			let splitter: Splitter = new LocatingSplitter("<", new FirstLocator())/*unknown*/;
			return new SplitComposable<Tuple2<CompileState, Node>>(splitter, Composable.toComposable((baseString: string, argsString: string) => {
				let argsTuple = ValueCompiler.values((state1: CompileState, s: string) => {
					return new OrRule<Node>(Lists.of((state2: CompileState, input1: string) => {
						return WhitespaceCompiler.parseWhitespace(state2, input1).map(type -  > new Tuple2Impl<?>(type.left(), type.right()))/*unknown*/;
					}, (state2: CompileState, type: string) => {
						return TypeCompiler.parseType(state2, type)/*unknown*/;
					})).apply(state1, s)/*unknown*/;
				}).apply(state, argsString).orElse(new Tuple2Impl<?>(state, Lists.empty()))/*unknown*/;
				let argsState = argsTuple.left()/*unknown*/;
				let args = argsTuple.right()/*unknown*/;
				let base = Strings.strip(baseString)/*unknown*/;
				return TypeCompiler.assembleFunctionNode(argsState, base, args).or(() => {
					let compileState = TypeCompiler.addResolvedImportFromCache0(argsState, base)/*unknown*/;
					return new Some<Tuple2<CompileState, Node>>(new Tuple2Impl<CompileState, Node>(compileState, new MapNode("template").withString("base", base).withNodeList("args", args)))/*unknown*/;
				})/*unknown*/;
			})).apply(withoutEnd)/*unknown*/;
		}).apply(Strings.strip(input))/*unknown*/;
	}
	static assembleFunctionNode(state: CompileState, base: string, args: List<Node>): Option<Tuple2<CompileState, Node>> {
		return TypeCompiler.mapFunctionNode(base, args).map((generated: Node) => {
			return new Tuple2Impl<CompileState, Node>(state, generated)/*unknown*/;
		})/*unknown*/;
	}
	static mapFunctionNode(base: string, args: List<Node>): Option<Node> {
		if (Strings.equalsTo("Function", base)/*unknown*/){
			return args.findFirst().and(() => {
				return args.find(1)/*unknown*/;
			}).map((tuple: Tuple2<Node, Node>) => {
				List < Node > args1/*unknown*/ = Lists.of(tuple.left())/*unknown*/;
				let returns: Node = tuple.right()/*unknown*/;
				return new MapNode("functional").withNodeList("args", args1).withNode("returns", returns)/*unknown*/;
			})/*unknown*/;
		}
		if (Strings.equalsTo("BiFunction", base)/*unknown*/){
			let args1: -> {
                        return args.find(1);
                    })
                    .and(() -> {
                        return args.find(2);
                    })
                    .map(tuple -> {
                        List<Node> = /* Lists.of(tuple.left().left(), tuple.left().right());
                        Node returns = tuple.right();
                        return new MapNode("functional")
                                .withNodeList("args", args1)
                                .withNode("returns", returns);
                    })*/;
		}
		if (Strings.equalsTo("Supplier", base)/*unknown*/){
			let args1: -> {
                List<Node> = /* Lists.empty();
                return new MapNode("functional")
                        .withNodeList("args", args1)
                        .withNode("returns", first);
            })*/;
		}
		if (Strings.equalsTo("Consumer", base)/*unknown*/){
			let args1: -> {
                List<Node> = /* Lists.of(first);
                return new MapNode("functional")
                        .withNodeList("args", args1)
                        .withNode("returns", TypeCompiler.Void);
            })*/;
		}
		if (Strings.equalsTo("Predicate", base)/*unknown*/){
			let args1: -> {
                List<Node> = /* Lists.of(first);
                return new MapNode("functional")
                        .withNodeList("args", args1)
                        .withNode("returns", TypeCompiler.Boolean);
            })*/;
		}
		return new None<Node>()/*unknown*/;
	}
	static parseNodeOrPlaceholder(state: CompileState, type: string): Tuple2<CompileState, Node> {
		return TypeCompiler.parseType(state, type).map((tuple: Tuple2<CompileState, Node>) => {
			return new Tuple2Impl<CompileState, Node>(tuple.left(), tuple.right())/*unknown*/;
		}).orElseGet(() => {
			return new Tuple2Impl<CompileState, Node>(state, new MapNode("placeholder").withString("value", type))/*unknown*/;
		})/*unknown*/;
	}
	static getState(immutableCompileState: CompileState, location: Location): CompileState {
		let requestedNamespace = location.namespace()/*unknown*/;
		let requestedChild = location.name()/*unknown*/;
		let namespace = TypeCompiler.fixNamespace(requestedNamespace, immutableCompileState.context().findNamespaceOrEmpty())/*unknown*/;
		if (immutableCompileState.registry().doesImportExistAlready(requestedChild)/*unknown*/){
			return immutableCompileState/*CompileState*/;
		}
		let namespaceWithChild = namespace.addLast(requestedChild)/*unknown*/;
		let anImport = new Import(namespaceWithChild, requestedChild)/*unknown*/;
		return immutableCompileState.mapRegistry((registry: Registry) => {
			return registry.addImport(anImport)/*unknown*/;
		})/*unknown*/;
	}
	static addResolvedImportFromCache0(state: CompileState, base: string): CompileState {
		if (state.stack().hasAnyStructureName(base)/*unknown*/){
			return state/*CompileState*/;
		}
		return state.context().findSource(base).map((source: Source) => {
			let location: Location = source.createLocation()/*unknown*/;
			return TypeCompiler.getCompileState1(state, location).orElseGet(() => {
				return TypeCompiler.getState(state, location)/*unknown*/;
			})/*unknown*/;
		}).orElse(state)/*unknown*/;
	}
	static getCompileState1(immutableCompileState: CompileState, location: Location): Option<CompileState> {
		if (!immutableCompileState/*CompileState*/.context().hasPlatform(Platform.PlantUML)/*unknown*/){
			return new None<CompileState>()/*unknown*/;
		}
		let name = immutableCompileState.context().findNameOrEmpty()/*unknown*/;
		let dependency = new Dependency(name, location.name())/*unknown*/;
		if (immutableCompileState.registry().containsDependency(dependency)/*unknown*/){
			return new None<CompileState>()/*unknown*/;
		}
		return new Some<CompileState>(immutableCompileState.mapRegistry((registry1: Registry) => {
			return registry1.addDependency(dependency)/*unknown*/;
		}))/*unknown*/;
	}
	static fixNamespace(requestedNamespace: List<string>, thisNamespace: List<string>): List<string> {
		if (thisNamespace.isEmpty()/*unknown*/){
			return requestedNamespace.addFirst(".")/*unknown*/;
		}
		return TypeCompiler.addParentSeparator(requestedNamespace, thisNamespace.size())/*unknown*/;
	}
	static addParentSeparator(newNamespace: List<string>, count: number): List<string> {
		let index = 0/*unknown*/;
		let copy = newNamespace/*List<string>*/;
		while (index < count/*unknown*/){
			copy/*unknown*/ = copy.addFirst("..")/*unknown*/;
			index/*unknown*/++;
		}
		return copy/*unknown*/;
	}
	static generateSimple(type: Node): string {
		if (type.is("functional")/*unknown*/){
			return TypeCompiler.generateType(type)/*unknown*/;
		}/*
        else if (type.is("placeholder")) {
            return ValueCompiler.generateValue(type);
        }*//*
        else if (TypeCompiler.variants.contains(type.findString("value").orElse(""))) {
            return TypeCompiler.generateType(type);
        }*//*
        else if (type.is("symbol")) {
            return ValueCompiler.generateValue(type);
        }*//*
        else if (type.is("template")) {
            return type.findString("base").orElse("");
        }*//*
        else if (type.is("variadic")) {
            return TypeCompiler.generateType(type);
        }*/
		return "?"/*unknown*/;
	}
	static generateBeforeName(type: Node): string {
		if (type.is("functional")/*unknown*/){
			return ""/*unknown*/;
		}/*
        else if (type.is("placeholder")) {
            return "";
        }*//*
        else if (TypeCompiler.variants.contains(type.findString("value").orElse(""))) {
            return "";
        }*//*
        else if (type.is("symbol")) {
            return "";
        }*//*
        else if (type.is("template")) {
            return "";
        }*//*
        else if (type.is("variadic")) {
            return "...";
        }*/
		/*throw new IllegalArgumentException()*/;
	}
	static generateType(type: Node): string {
		if (type.is("functional")/*unknown*/){
			let joinedArguments = TypeCompiler.generateFunctionalArguments(type)/*unknown*/;
			let returns: Node = type.findNode("returns").orElse(new MapNode())/*unknown*/;
			return "(" + joinedArguments + ") => " + TypeCompiler.generateType(returns)/*unknown*/;
		}
		if (type.is("placeholder")/*unknown*/){
			let input = type.findString("value").orElse("")/*unknown*/;
			return Placeholders.generatePlaceholder(input)/*unknown*/;
		}
		if (TypeCompiler.variants.contains(type.findString("value").orElse(""))/*unknown*/){
			return type.findString("value").orElse("")/*unknown*/;
		}
		if (type.is("symbol")/*unknown*/){
			return type.findString("value").orElse("")/*unknown*/;
		}
		if (type.is("template")/*unknown*/){
			let base = type.findString("base").orElse("")/*unknown*/;
			let joined: string = TypeCompiler.joinTemplateArguments(type)/*unknown*/;
			return base + "<" + joined + ">"/*unknown*/;
		}
		if (type.is("variadic")/*unknown*/){
			let child: Node = type.findNode("child").orElse(new MapNode())/*unknown*/;
			return TypeCompiler.generateType(child) + "[]"/*unknown*/;
		}
		return "?"/*unknown*/;
	}/*

    private static java.lang.String joinTemplateArguments(Node type) {
        return type.findNodeList("args").orElse(Lists.empty()).iter()
                .map((Node arg) -> TypeCompiler.generateType(arg))
                .collect(new Joiner(", "))
                .orElse("");
    }*/
	static generateFunctionalArguments(type: Node): string {
		return type.findNodeList("args").orElse(Lists.empty()).iterWithIndices().map((tuple: Tuple2<number, Node>) => "arg" + tuple.left() + " : " + TypeCompiler.generateType(tuple.right())/*unknown*/).collect(new Joiner(", ")).orElse("")/*unknown*/;
	}
}
