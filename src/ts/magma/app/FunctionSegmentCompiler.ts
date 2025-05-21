import { CompileState } from "../../magma/app/compile/CompileState";
import { Tuple2 } from "../../magma/api/Tuple2";
import { Option } from "../../magma/api/option/Option";
import { Strings } from "../../magma/api/text/Strings";
import { Some } from "../../magma/api/option/Some";
import { Tuple2Impl } from "../../magma/api/Tuple2Impl";
import { None } from "../../magma/api/option/None";
import { SuffixComposable } from "../../magma/app/compile/compose/SuffixComposable";
import { SplitComposable } from "../../magma/app/compile/compose/SplitComposable";
import { LastSelector } from "../../magma/app/compile/select/LastSelector";
import { Selector } from "../../magma/app/compile/select/Selector";
import { FoldingSplitter } from "../../magma/app/compile/split/FoldingSplitter";
import { DivideState } from "../../magma/app/compile/DivideState";
import { Composable } from "../../magma/app/compile/compose/Composable";
import { OrRule } from "../../magma/app/compile/rule/OrRule";
import { Lists } from "../../jvm/api/collect/list/Lists";
import { Rule } from "../../magma/app/compile/rule/Rule";
import { PrefixComposable } from "../../magma/app/compile/compose/PrefixComposable";
import { ValueCompiler } from "../../magma/app/ValueCompiler";
import { Value } from "../../magma/app/compile/value/Value";
import { LocatingSplitter } from "../../magma/app/compile/split/LocatingSplitter";
import { FirstLocator } from "../../magma/app/compile/locate/FirstLocator";
import { Splitter } from "../../magma/app/compile/split/Splitter";
import { DefiningCompiler } from "../../magma/app/DefiningCompiler";
import { Definition } from "../../magma/app/compile/define/Definition";
import { Placeholder } from "../../magma/app/compile/value/Placeholder";
import { WhitespaceCompiler } from "../../magma/app/WhitespaceCompiler";
import { DivideRule } from "../../magma/app/DivideRule";
import { StatementsFolder } from "../../magma/app/compile/fold/StatementsFolder";
import { List } from "../../magma/api/collect/list/List";
import { StatementsMerger } from "../../magma/app/compile/merge/StatementsMerger";
import { Merger } from "../../magma/app/compile/merge/Merger";
export class FunctionSegmentCompiler {
	static compileEmptySegment(state: CompileState, input: string): Option<Tuple2<CompileState, string>> {
		if (Strings.equalsTo(";", Strings.strip(input))/*unknown*/){
			return new Some<Tuple2<CompileState, string>>(new Tuple2Impl<CompileState, string>(state, ";"))/*unknown*/;
		}
		else {
			return new None<Tuple2<CompileState, string>>()/*unknown*/;
		}
	}
	static compileBlock(state: CompileState, input: string): Option<Tuple2<CompileState, string>> {
		return new SuffixComposable<Tuple2<CompileState, string>>("}", (withoutEnd: string) => new SplitComposable<Tuple2<CompileState, string>>((withoutEnd0: string) => {
			let selector: Selector = new LastSelector("")/*unknown*/;
			return new FoldingSplitter((state1: DivideState, c: string) => FunctionSegmentCompiler.foldBlockStarts(state1, c)/*unknown*/, selector).apply(withoutEnd0)/*unknown*/;
		}, Composable.toComposable((beforeContentWithEnd: string, content: string) => new SuffixComposable<Tuple2<CompileState, string>>("{", (beforeContent: string) => FunctionSegmentCompiler.compileBlockHeader(state, beforeContent).flatMap((headerTuple: Tuple2<CompileState, string>) => {
			let contentTuple = FunctionSegmentCompiler.compileFunctionStatements(headerTuple.left().enterDepth(), content)/*unknown*/;
			let indent = state.createIndent()/*unknown*/;
			return new Some<Tuple2<CompileState, string>>(new Tuple2Impl<CompileState, string>(contentTuple.left().exitDepth(), indent + headerTuple.right() + "{" + contentTuple.right() + indent + "}"))/*unknown*/;
		})/*unknown*/).apply(beforeContentWithEnd)/*unknown*/)).apply(withoutEnd)/*unknown*/).apply(Strings.strip(input))/*unknown*/;
	}
	static foldBlockStarts(state: DivideState, c: string): DivideState {
		let appended = state.append(c)/*unknown*/;
		if ("{" === c/*unknown*/){
			let entered = appended.enter()/*unknown*/;
			if (entered.isShallow()/*unknown*/){
				return entered.advance()/*unknown*/;
			}
			else {
				return entered/*unknown*/;
			}
		}
		if ("}" === c/*unknown*/){
			return appended.exit()/*unknown*/;
		}
		return appended/*unknown*/;
	}
	static compileBlockHeader(state: CompileState, input: string): Option<Tuple2<CompileState, string>> {
		return new OrRule<string>(Lists.of(FunctionSegmentCompiler.createConditionalRule("if"), FunctionSegmentCompiler.createConditionalRule("while"), FunctionSegmentCompiler.compileElse)).apply(state, input)/*unknown*/;
	}
	static createConditionalRule(prefix: string): Rule<string> {
		return (state1: CompileState, input1: string) => new PrefixComposable<Tuple2<CompileState, string>>(prefix, (withoutPrefix: string) => {
			let strippedCondition = Strings.strip(withoutPrefix)/*unknown*/;
			return new PrefixComposable<Tuple2<CompileState, string>>("(", (withoutConditionStart: string) => new SuffixComposable<Tuple2<CompileState, string>>(")", (withoutConditionEnd: string) => {
				let tuple = ValueCompiler.compileValueOrPlaceholder(state1, withoutConditionEnd)/*unknown*/;
				return new Some<Tuple2<CompileState, string>>(new Tuple2Impl<CompileState, string>(tuple.left(), prefix + " (" + tuple.right() + ")"))/*unknown*/;
			}).apply(withoutConditionStart)/*unknown*/).apply(strippedCondition)/*unknown*/;
		}).apply(Strings.strip(input1))/*unknown*//*unknown*/;
	}
	static compileElse(state: CompileState, input: string): Option<Tuple2<CompileState, string>> {
		if (Strings.equalsTo("else", Strings.strip(input))/*unknown*/){
			return new Some<Tuple2<CompileState, string>>(new Tuple2Impl<CompileState, string>(state, "else "))/*unknown*/;
		}
		else {
			return new None<Tuple2<CompileState, string>>()/*unknown*/;
		}
	}
	static compileFunctionStatement(state: CompileState, input: string): Option<Tuple2<CompileState, string>> {
		return new SuffixComposable<Tuple2<CompileState, string>>(";", (withoutEnd: string) => {
			let valueTuple = FunctionSegmentCompiler.compileFunctionStatementValue(state, withoutEnd)/*unknown*/;
			return new Some<Tuple2<CompileState, string>>(new Tuple2Impl<CompileState, string>(valueTuple.left(), state.createIndent() + valueTuple.right() + ";"))/*unknown*/;
		}).apply(Strings.strip(input))/*unknown*/;
	}
	static compileFunctionStatementValue(state: CompileState, withoutEnd: string): Tuple2<CompileState, string> {
		return OrRule.compileOrPlaceholder(state, withoutEnd, Lists.of(FunctionSegmentCompiler.compileReturnWithValue, FunctionSegmentCompiler.compileAssignment, FunctionSegmentCompiler.createInvokableRule(), FunctionSegmentCompiler.createPostRule("++"), FunctionSegmentCompiler.createPostRule("--"), FunctionSegmentCompiler.compileBreak))/*unknown*/;
	}
	static createInvokableRule(): Rule<string> {
		return (state1: CompileState, input: string) => ValueCompiler.parseInvokable(state1, input).map((tuple: Tuple2<CompileState, Value>) => {
			return ValueCompiler.generateValue(tuple)/*unknown*/;
		})/*unknown*//*unknown*/;
	}
	static compileBreak(state: CompileState, input: string): Option<Tuple2<CompileState, string>> {
		if (Strings.equalsTo("break", Strings.strip(input))/*unknown*/){
			return new Some<Tuple2<CompileState, string>>(new Tuple2Impl<CompileState, string>(state, "break"))/*unknown*/;
		}
		else {
			return new None<Tuple2<CompileState, string>>()/*unknown*/;
		}
	}
	static createPostRule(suffix: string): Rule<string> {
		return (state1: CompileState, input: string) => new SuffixComposable<Tuple2<CompileState, string>>(suffix, (child: string) => {
			let tuple = ValueCompiler.compileValueOrPlaceholder(state1, child)/*unknown*/;
			return new Some<Tuple2<CompileState, string>>(new Tuple2Impl<CompileState, string>(tuple.left(), tuple.right() + suffix))/*unknown*/;
		}).apply(Strings.strip(input))/*unknown*//*unknown*/;
	}
	static compileReturnWithValue(state: CompileState, input: string): Option<Tuple2<CompileState, string>> {
		return FunctionSegmentCompiler.compileReturn(input, (value1: string) => ValueCompiler.compileValue(state, value1)/*unknown*/)/*unknown*/;
	}
	static compileReturn(input: string, mapper: (arg0 : string) => Option<Tuple2<CompileState, string>>): Option<Tuple2<CompileState, string>> {
		return new PrefixComposable<Tuple2<CompileState, string>>("return ", (value: string) => mapper(value).flatMap((tuple: Tuple2<CompileState, string>) => new Some<Tuple2<CompileState, string>>(new Tuple2Impl<CompileState, string>(tuple.left(), "return " + tuple.right()))/*unknown*/)/*unknown*/).apply(Strings.strip(input))/*unknown*/;
	}
	static compileReturnWithoutSuffix(state1: CompileState, input1: string): Option<Tuple2<CompileState, string>> {
		return FunctionSegmentCompiler.compileReturn(input1, (withoutPrefix: string) => ValueCompiler.compileValue(state1, withoutPrefix)/*unknown*/).map((tuple: Tuple2<CompileState, string>) => new Tuple2Impl<CompileState, string>(tuple.left(), state1.createIndent() + tuple.right())/*unknown*/)/*unknown*/;
	}
	static compileAssignment(state: CompileState, input: string): Option<Tuple2<CompileState, string>> {
		let splitter: Splitter = new LocatingSplitter("=", new FirstLocator())/*unknown*/;
		return new SplitComposable<Tuple2<CompileState, string>>(splitter, Composable.toComposable((destination: string, source: string) => {
			let sourceTuple = ValueCompiler.compileValueOrPlaceholder(state, source)/*unknown*/;
			let destinationTuple = ValueCompiler.compileValue(sourceTuple.left(), destination).or(() => DefiningCompiler.parseDefinition(sourceTuple.left(), destination).map((tuple: Tuple2<CompileState, Definition>) => new Tuple2Impl<CompileState, string>(tuple.left(), "let " + tuple.right().generate())/*unknown*/)/*unknown*/).orElseGet(() => new Tuple2Impl<CompileState, string>(sourceTuple.left(), Placeholder.generatePlaceholder(destination))/*unknown*/)/*unknown*/;
			return new Some<Tuple2<CompileState, string>>(new Tuple2Impl<CompileState, string>(destinationTuple.left(), destinationTuple.right() + " = " + sourceTuple.right()))/*unknown*/;
		})).apply(input)/*unknown*/;
	}
	static compileFunctionStatements(state: CompileState, input: string): Tuple2<CompileState, string> {
		return FunctionSegmentCompiler.compileStatements(state, input, FunctionSegmentCompiler.compileFunctionSegment)/*unknown*/;
	}
	static compileFunctionSegment(state: CompileState, input: string): Tuple2<CompileState, string> {
		return OrRule.compileOrPlaceholder(state, input, Lists.of(WhitespaceCompiler.compileWhitespace, FunctionSegmentCompiler.compileEmptySegment, FunctionSegmentCompiler.compileBlock, FunctionSegmentCompiler.compileFunctionStatement, FunctionSegmentCompiler.compileReturnWithoutSuffix))/*unknown*/;
	}
	static compileStatements(state: CompileState, input: string, mapper: (arg0 : CompileState, arg1 : string) => Tuple2<CompileState, string>): Tuple2<CompileState, string> {
		return new DivideRule<string>(new StatementsFolder(), toRule(mapper)).apply(state, input).map((folded: Tuple2<CompileState, List<string>>) => {
			return generateAllFromTuple(folded.left(), folded.right(), new StatementsMerger())/*unknown*/;
		}).orElse(new Tuple2Impl<CompileState, string>(state, ""))/*unknown*/;
	}
	static toRule(mapper: (arg0 : CompileState, arg1 : string) => Tuple2<CompileState, string>): Rule<string> {
		return (state1: CompileState, s: string) => {
			return new Some<Tuple2<CompileState, string>>(mapper(state1, s))/*unknown*/;
		}/*unknown*/;
	}
	static generateAllFromTuple(state: CompileState, elements: List<string>, merger: Merger): Tuple2<CompileState, string> {
		return new Tuple2Impl<CompileState, string>(state, Merger.generateAll(elements, merger))/*unknown*/;
	}
}
