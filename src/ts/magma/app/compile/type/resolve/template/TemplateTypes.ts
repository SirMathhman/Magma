import { CompileState } from "../../../../../../magma/app/compile/CompileState";
import { Type } from "../../../../../../magma/app/compile/type/Type";
import { Tuple2 } from "../../../../../../magma/api/Tuple2";
import { Option } from "../../../../../../magma/api/option/Option";
import { SuffixComposable } from "../../../../../../magma/app/compile/compose/SuffixComposable";
import { LocatingSplitter } from "../../../../../../magma/app/compile/split/LocatingSplitter";
import { FirstLocator } from "../../../../../../magma/app/compile/locate/FirstLocator";
import { Splitter } from "../../../../../../magma/app/compile/split/Splitter";
import { SplitComposable } from "../../../../../../magma/app/compile/compose/SplitComposable";
import { Composable } from "../../../../../../magma/app/compile/compose/Composable";
import { ValueCompiler } from "../../../../../../magma/app/ValueCompiler";
import { List } from "../../../../../../magma/api/collect/list/List";
import { Tuple2Impl } from "../../../../../../magma/api/Tuple2Impl";
import { Lists } from "../../../../../../jvm/api/collect/list/Lists";
import { Strings } from "../../../../../../magma/api/text/Strings";
import { ResolvedTypes } from "../../../../../../magma/app/compile/type/resolve/ResolvedTypes";
import { Some } from "../../../../../../magma/api/option/Some";
import { TemplateType } from "../../../../../../magma/app/compile/type/resolve/template/TemplateType";
import { FunctionType } from "../../../../../../magma/app/compile/type/resolve/template/FunctionType";
import { None } from "../../../../../../magma/api/option/None";
import { OrRule } from "../../../../../../magma/app/compile/rule/OrRule";
import { WhitespaceCompiler } from "../../../../../../magma/app/WhitespaceCompiler";
import { TypeCompiler } from "../../../../../../magma/app/TypeCompiler";
import { Merger } from "../../../../../../magma/app/compile/merge/Merger";
import { ValueMerger } from "../../../../../../magma/app/compile/merge/ValueMerger";
export class TemplateTypes {
	static parseGeneric(state: CompileState, input: string): Option<Tuple2<CompileState, Type>> {
		return new SuffixComposable<Tuple2<CompileState, Type>>(">", (withoutEnd: string) => {
			let splitter: Splitter = new LocatingSplitter("<", new FirstLocator())/*unknown*/;
			return new SplitComposable<Tuple2<CompileState, Type>>(splitter, Composable.toComposable((baseString: string, argsString: string) => {
				let argsTuple = ValueCompiler.values((state1: CompileState, s: string) => TemplateTypes.compileTypeArgument(state1, s)/*unknown*/).apply(state, argsString).orElse(new Tuple2Impl<CompileState, List<string>>(state, Lists.empty()))/*unknown*/;
				let argsState = argsTuple.left()/*unknown*/;
				let args = argsTuple.right()/*unknown*/;
				let base = Strings.strip(baseString)/*unknown*/;
				return TemplateTypes.assembleFunctionType(argsState, base, args).or(() => {
					let compileState = ResolvedTypes.addResolvedImportFromCache0(argsState, base)/*unknown*/;
					return new Some<Tuple2<CompileState, Type>>(new Tuple2Impl<CompileState, Type>(compileState, new TemplateType(base, args)))/*unknown*/;
				})/*unknown*/;
			})).apply(withoutEnd)/*unknown*/;
		}).apply(Strings.strip(input))/*unknown*/;
	}
	static assembleFunctionType(state: CompileState, base: string, args: List<string>): Option<Tuple2<CompileState, Type>> {
		return TemplateTypes.mapFunctionType(base, args).map((generated: Type) => new Tuple2Impl<CompileState, Type>(state, generated)/*unknown*/)/*unknown*/;
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
		return new OrRule<string>(Lists.of((state2: CompileState, input1: string) => WhitespaceCompiler.createWhitespaceRule().apply(state2, input1)/*unknown*/, (state1: CompileState, type: string) => TypeCompiler.createTypeRule().apply(state1, type).map((tuple: Tuple2<CompileState, Type>) => new Tuple2Impl<CompileState, string>(tuple.left(), TypeCompiler.generateType(tuple.right()))/*unknown*/)/*unknown*/)).apply(state, input)/*unknown*/;
	}
	static generateTemplateType(templateType: TemplateType): string {
		return templateType.base() + "<" + Merger.generateAll(templateType.args(), new ValueMerger()) + ">"/*unknown*/;
	}
}
