import { CompileState } from "../../magma/app/compile/CompileState";
import { Tuple2 } from "../../magma/api/Tuple2";
import { Option } from "../../magma/api/option/Option";
import { Type } from "../../magma/app/compile/type/Type";
import { Tuple2Impl } from "../../magma/api/Tuple2Impl";
import { OrRule } from "../../magma/app/compile/rule/OrRule";
import { Lists } from "../../jvm/api/collect/list/Lists";
import { Symbols } from "../../magma/app/compile/symbol/Symbols";
import { Strings } from "../../magma/api/text/Strings";
import { SuffixComposable } from "../../magma/app/compile/compose/SuffixComposable";
import { Some } from "../../magma/api/option/Some";
import { VariadicType } from "../../magma/app/compile/type/VariadicType";
import { PrimitiveType } from "../../magma/app/compile/type/PrimitiveType";
import { None } from "../../magma/api/option/None";
import { LocatingSplitter } from "../../magma/app/compile/split/LocatingSplitter";
import { FirstLocator } from "../../magma/app/compile/locate/FirstLocator";
import { Splitter } from "../../magma/app/compile/split/Splitter";
import { SplitComposable } from "../../magma/app/compile/compose/SplitComposable";
import { Composable } from "../../magma/app/compile/compose/Composable";
import { ValueCompiler } from "../../magma/app/ValueCompiler";
import { List } from "../../magma/api/collect/list/List";
import { TemplateType } from "../../magma/app/compile/type/TemplateType";
import { FunctionType } from "../../magma/app/compile/type/FunctionType";
import { WhitespaceCompiler } from "../../magma/app/WhitespaceCompiler";
import { Placeholder } from "../../magma/app/compile/value/Placeholder";
import { Location } from "../../magma/app/Location";
import { Import } from "../../magma/app/compile/Import";
import { Registry } from "../../magma/app/compile/Registry";
import { Source } from "../../magma/app/io/Source";
import { Platform } from "../../magma/app/Platform";
import { Dependency } from "../../magma/app/compile/Dependency";
import { Merger } from "../../magma/app/compile/merge/Merger";
import { ValueMerger } from "../../magma/app/compile/merge/ValueMerger";
import { Symbol } from "../../magma/app/compile/value/Symbol";
import { Joiner } from "../../magma/api/collect/Joiner";
export class TypeCompiler {
	static compileType(state: CompileState, type: string): Option<Tuple2<CompileState, string>> {
		return TypeCompiler.parseType(state, type).map((tuple: Tuple2<CompileState, Type>) => new Tuple2Impl<CompileState, string>(tuple.left(), TypeCompiler.generateType(tuple.right()))/*unknown*/)/*unknown*/;
	}
	static parseType(state: CompileState, type: string): Option<Tuple2<CompileState, Type>> {
		return new OrRule<Type>(Lists.of(TypeCompiler.parseVarArgs, TypeCompiler.parseGeneric, TypeCompiler.parsePrimitive, Symbols.parseSymbolType)).apply(state, type)/*unknown*/;
	}
	static parseVarArgs(state: CompileState, input: string): Option<Tuple2<CompileState, Type>> {
		let stripped = Strings.strip(input)/*unknown*/;
		return new SuffixComposable<Tuple2<CompileState, Type>>("...", (s: string) => {
			let child = TypeCompiler.parseTypeOrPlaceholder(state, s)/*unknown*/;
			return new Some<Tuple2<CompileState, Type>>(new Tuple2Impl<CompileState, Type>(child.left(), new VariadicType(child.right())))/*unknown*/;
		}).apply(stripped)/*unknown*/;
	}
	static parsePrimitive(state: CompileState, input: string): Option<Tuple2<CompileState, Type>> {
		return TypeCompiler.findPrimitiveValue(Strings.strip(input)).map((result: Type) => new Tuple2Impl<CompileState, Type>(state, result)/*unknown*/)/*unknown*/;
	}
	static findPrimitiveValue(input: string): Option<Type> {
		let stripped = Strings.strip(input)/*unknown*/;
		if (Strings.equalsTo("char", stripped) || Strings.equalsTo("Character", stripped) || Strings.equalsTo("String", stripped)/*unknown*/){
			return new Some<Type>(PrimitiveType.String)/*unknown*/;
		}
		if (Strings.equalsTo("int", stripped) || Strings.equalsTo("Integer", stripped)/*unknown*/){
			return new Some<Type>(PrimitiveType.Number)/*unknown*/;
		}
		if (Strings.equalsTo("boolean", stripped) || Strings.equalsTo("Boolean", stripped)/*unknown*/){
			return new Some<Type>(PrimitiveType.Boolean)/*unknown*/;
		}
		if (Strings.equalsTo("var", stripped)/*unknown*/){
			return new Some<Type>(PrimitiveType.Var)/*unknown*/;
		}
		if (Strings.equalsTo("void", stripped)/*unknown*/){
			return new Some<Type>(PrimitiveType.Void)/*unknown*/;
		}
		return new None<Type>()/*unknown*/;
	}
	static parseGeneric(state: CompileState, input: string): Option<Tuple2<CompileState, Type>> {
		return new SuffixComposable<Tuple2<CompileState, Type>>(">", (withoutEnd: string) => {
			let splitter: Splitter = new LocatingSplitter("<", new FirstLocator())/*unknown*/;
			return new SplitComposable<Tuple2<CompileState, Type>>(splitter, Composable.toComposable((baseString: string, argsString: string) => {
				let argsTuple = ValueCompiler.values((state1: CompileState, s: string) => TypeCompiler.compileTypeArgument(state1, s)/*unknown*/).apply(state, argsString).orElse(new Tuple2Impl<CompileState, List<string>>(state, Lists.empty()))/*unknown*/;
				let argsState = argsTuple.left()/*unknown*/;
				let args = argsTuple.right()/*unknown*/;
				let base = Strings.strip(baseString)/*unknown*/;
				return TypeCompiler.assembleFunctionType(argsState, base, args).or(() => {
					let compileState = TypeCompiler.addResolvedImportFromCache0(argsState, base)/*unknown*/;
					return new Some<Tuple2<CompileState, Type>>(new Tuple2Impl<CompileState, Type>(compileState, new TemplateType(base, args)))/*unknown*/;
				})/*unknown*/;
			})).apply(withoutEnd)/*unknown*/;
		}).apply(Strings.strip(input))/*unknown*/;
	}
	static assembleFunctionType(state: CompileState, base: string, args: List<string>): Option<Tuple2<CompileState, Type>> {
		return TypeCompiler.mapFunctionType(base, args).map((generated: Type) => new Tuple2Impl<CompileState, Type>(state, generated)/*unknown*/)/*unknown*/;
	}
	static mapFunctionType(base: string, args: List<string>): Option<Type> {
		if (Strings.equalsTo("Function", base)/*unknown*/){
			return args.findFirst().and(() => args.find(1)/*unknown*/).map((tuple: Tuple2<string, string>) => new FunctionType(Lists.of(tuple.left()), tuple.right())/*unknown*/)/*unknown*/;
		}
		if (Strings.equalsTo("BiFunction", base)/*unknown*/){
			return args.find(0).and(() => args.find(1)/*unknown*/).and(() => args.find(2)/*unknown*/).map((tuple: Tuple2<Tuple2<string, string>, string>) => new FunctionType(Lists.of(tuple.left().left(), tuple.left().right()), tuple.right())/*unknown*/)/*unknown*/;
		}
		if (Strings.equalsTo("Supplier", base)/*unknown*/){
			return args.findFirst().map((first: string) => new FunctionType(Lists.empty(), first)/*unknown*/)/*unknown*/;
		}
		if (Strings.equalsTo("Consumer", base)/*unknown*/){
			return args.findFirst().map((first: string) => new FunctionType(Lists.of(first), "void")/*unknown*/)/*unknown*/;
		}
		if (Strings.equalsTo("Predicate", base)/*unknown*/){
			return args.findFirst().map((first: string) => new FunctionType(Lists.of(first), "boolean")/*unknown*/)/*unknown*/;
		}
		return new None<Type>()/*unknown*/;
	}
	static compileTypeArgument(state: CompileState, input: string): Option<Tuple2<CompileState, string>> {
		return new OrRule<string>(Lists.of((state2: CompileState, input1: string) => WhitespaceCompiler.compileWhitespace(state2, input1)/*unknown*/, (state1: CompileState, type: string) => TypeCompiler.compileType(state1, type)/*unknown*/)).apply(state, input)/*unknown*/;
	}
	static parseTypeOrPlaceholder(state: CompileState, type: string): Tuple2<CompileState, Type> {
		return TypeCompiler.parseType(state, type).map((tuple: Tuple2<CompileState, Type>) => new Tuple2Impl<CompileState, Type>(tuple.left(), tuple.right())/*unknown*/).orElseGet(() => new Tuple2Impl<CompileState, Type>(state, new Placeholder(type))/*unknown*/)/*unknown*/;
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
		return immutableCompileState.mapRegistry((registry: Registry) => registry.addImport(anImport)/*unknown*/)/*unknown*/;
	}
	static addResolvedImportFromCache0(state: CompileState, base: string): CompileState {
		if (state.stack().hasAnyStructureName(base)/*unknown*/){
			return state/*CompileState*/;
		}
		return state.context().findSource(base).map((source: Source) => {
			let location: Location = source.createLocation()/*unknown*/;
			return TypeCompiler.getCompileState1(state, location).orElseGet(() => TypeCompiler.getState(state, location)/*unknown*/)/*unknown*/;
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
		return new Some<CompileState>(immutableCompileState.mapRegistry((registry1: Registry) => registry1.addDependency(dependency)/*unknown*/))/*unknown*/;
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
	static generateType(type: Type): string {/*return switch (type) {
            case FunctionType functionType -> TypeCompiler.generateFunctionType(functionType);
            case Placeholder placeholder -> TypeCompiler.generatePlaceholder(placeholder);
            case PrimitiveType primitiveType -> TypeCompiler.generatePrimitiveType(primitiveType);
            case Symbol symbol -> TypeCompiler.generateSymbol(symbol);
            case TemplateType templateType -> TypeCompiler.generateTemplateType(templateType);
            case VariadicType variadicType -> TypeCompiler.generateVariadicType(variadicType);
            default -> "?";
        }*/;
	}
	static generateVariadicType(variadicType: VariadicType): string {
		return TypeCompiler.generateType(variadicType.type()) + "[]"/*unknown*/;
	}
	static generateTemplateType(templateType: TemplateType): string {
		return templateType.base() + "<" + Merger.generateAll(templateType.args(), new ValueMerger()) + ">"/*unknown*/;
	}
	static generateSymbol(symbol: Symbol): string {
		return symbol.value()/*unknown*/;
	}
	static generatePrimitiveType(primitiveType: PrimitiveType): string {
		return primitiveType.value/*unknown*/;
	}
	static generatePlaceholder(placeholder: Placeholder): string {
		return Placeholder.generatePlaceholder(placeholder.input())/*unknown*/;
	}
	static generateFunctionType(functionType: FunctionType): string {
		let joinedArguments = functionType.args().iterWithIndices().map((tuple: Tuple2<number, string>) => "arg" + tuple.left() + " : " + tuple.right()/*unknown*/).collect(new Joiner(", ")).orElse("")/*unknown*/;
		return "(" + joinedArguments + ") => " + functionType.returns()/*unknown*/;
	}
}
