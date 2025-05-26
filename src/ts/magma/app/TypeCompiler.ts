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
import { VariadicType } from "../../magma/app/compile/type/VariadicType";
import { ValueCompiler } from "../../magma/app/ValueCompiler";
import { MapNode } from "../../magma/app/compile/node/MapNode";
import { None } from "../../magma/api/option/None";
import { PrimitiveNode } from "../../magma/app/compile/type/PrimitiveNode";
import { LocatingSplitter } from "../../magma/app/compile/split/LocatingSplitter";
import { FirstLocator } from "../../magma/app/compile/locate/FirstLocator";
import { Splitter } from "../../magma/app/compile/split/Splitter";
import { SplitComposable } from "../../magma/app/compile/compose/SplitComposable";
import { Composable } from "../../magma/app/compile/compose/Composable";
import { WhitespaceCompiler } from "../../magma/app/WhitespaceCompiler";
import { TemplateNode } from "../../magma/app/compile/type/TemplateNode";
import { List } from "../../magma/api/collect/list/List";
import { FunctionType } from "../../magma/app/compile/type/FunctionType";
import { Placeholder } from "../../magma/app/compile/type/Placeholder";
import { Location } from "../../magma/app/Location";
import { Import } from "../../magma/app/compile/Import";
import { Registry } from "../../magma/app/compile/Registry";
import { Source } from "../../magma/app/io/Source";
import { Platform } from "../../magma/app/Platform";
import { Dependency } from "../../magma/app/compile/Dependency";
export class TypeCompiler {
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
			return new Some<Tuple2<CompileState, Node>>(new Tuple2Impl<CompileState, Node>(child.left(), new VariadicType(child.right())))/*unknown*/;
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
			return new Some<Node>(PrimitiveNode.String)/*unknown*/;
		}
		if (Strings.equalsTo("int", stripped) || Strings.equalsTo("Integer", stripped)/*unknown*/){
			return new Some<Node>(PrimitiveNode.Number)/*unknown*/;
		}
		if (Strings.equalsTo("boolean", stripped) || Strings.equalsTo("Boolean", stripped)/*unknown*/){
			return new Some<Node>(PrimitiveNode.Boolean)/*unknown*/;
		}
		if (Strings.equalsTo("var", stripped)/*unknown*/){
			return new Some<Node>(PrimitiveNode.Var)/*unknown*/;
		}
		if (Strings.equalsTo("void", stripped)/*unknown*/){
			return new Some<Node>(PrimitiveNode.Void)/*unknown*/;
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
					return new Some<Tuple2<CompileState, Node>>(new Tuple2Impl<CompileState, Node>(compileState, new TemplateNode(base, args)))/*unknown*/;
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
				return FunctionType.createFunctionType(Lists.of(tuple.left()), tuple.right())/*unknown*/;
			})/*unknown*/;
		}
		if (Strings.equalsTo("BiFunction", base)/*unknown*/){
			/*return args.find(0)
                    .and(() -> {
                        return args.find(1);
                    })
                    .and(() -> {
                        return args.find(2);
                    })
                    .map(tuple -> {
                        return FunctionType.createFunctionType(Lists.of(tuple.left().left(), tuple.left().right()), tuple.right());
                    })*/;
		}
		if (Strings.equalsTo("Supplier", base)/*unknown*/){
			/*return args.findFirst().map((first) -> {
                return FunctionType.createFunctionType(Lists.empty(), first);
            })*/;
		}
		if (Strings.equalsTo("Consumer", base)/*unknown*/){
			/*return args.findFirst().map((first) -> {
                return FunctionType.createFunctionType(Lists.of(first), PrimitiveNode.Void);
            })*/;
		}
		if (Strings.equalsTo("Predicate", base)/*unknown*/){
			/*return args.findFirst().map((first) -> {
                return FunctionType.createFunctionType(Lists.of(first), PrimitiveNode.Boolean);
            })*/;
		}
		return new None<Node>()/*unknown*/;
	}
	static parseNodeOrPlaceholder(state: CompileState, type: string): Tuple2<CompileState, Node> {
		return TypeCompiler.parseType(state, type).map((tuple: Tuple2<CompileState, Node>) => {
			return new Tuple2Impl<CompileState, Node>(tuple.left(), tuple.right())/*unknown*/;
		}).orElseGet(() => {
			return new Tuple2Impl<CompileState, Node>(state, new Placeholder(type))/*unknown*/;
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
		if (/*Objects.requireNonNull(type) instanceof FunctionType functionNode*/){
			return TypeCompiler.generateType(functionNode)/*unknown*/;
		}/*
        else if (type instanceof Placeholder placeholder) {
            return placeholder.generateSimple();
        }*//*
        else if (type instanceof PrimitiveNode primitiveNode) {
            return primitiveNode.generateSimple();
        }*//*
        else if (type.is("symbol")) {
            return ValueCompiler.generateValue(type);
        }*//*
        else if (type instanceof TemplateNode templateNode) {
            return templateNode.base();
        }*//*
        else if (type instanceof VariadicType variadicNode) {
            return variadicNode.generateSimple();
        }*/
		return "?"/*unknown*/;
	}
	static generateBeforeName(type: Node): string {
		if (/*Objects.requireNonNull(type) instanceof FunctionType functionNode*/){
			return ""/*unknown*/;
		}/*
        else if (type instanceof Placeholder placeholder) {
            return placeholder.generateBeforeName();
        }*//*
        else if (type instanceof PrimitiveNode primitiveNode) {
            return primitiveNode.generateBeforeName();
        }*//*
        else if (type.is("symbol")) {
            return "";
        }*//*
        else if (type instanceof TemplateNode templateNode) {
            return "";
        }*//*
        else if (type instanceof VariadicType variadicNode) {
            return variadicNode.generateBeforeName();
        }*/
		/*throw new IllegalArgumentException()*/;
	}
	static generateType(type: Node): string {
		if (/*Objects.requireNonNull(type) instanceof FunctionType functionNode*/){
			let joinedArguments = /* functionNode.args()
                    .iterWithIndices()
                    .map((tuple) -> {
                        return "arg" + tuple.left() + " : " + TypeCompiler.generateType(tuple.right());
                    })
                    .collect(new Joiner(", "))
                    .orElse("")*/;
			return "(" + joinedArguments + ") => " + TypeCompiler.generateType(functionNode.returns())/*unknown*/;
		}/*
        else if (type instanceof Placeholder placeholder) {
            return placeholder.generateNode();
        }*//*
        else if (type instanceof PrimitiveNode primitiveNode) {
            return primitiveNode.generateNode();
        }*//*
        else if (type.is("symbol")) {
            return type.findString("value").orElse("");
        }*//*
        else if (type instanceof TemplateNode templateNode) {
            String joined = templateNode.args().iter()
                    .map(arg -> generateType(arg))
                    .collect(new Joiner(", "))
                    .orElse("");

            return templateNode.base() + "<" + joined + ">";
        }*//*
        else if (type instanceof VariadicType variadicNode) {
            return variadicNode.generateNode();
        }*/
		return "?"/*unknown*/;
	}
}
