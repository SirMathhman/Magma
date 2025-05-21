import { CompileState } from "../../magma/app/compile/CompileState";
import { Tuple2Impl } from "../../magma/api/Tuple2Impl";
import { Value } from "../../magma/app/compile/value/Value";
import { Tuple2 } from "../../magma/api/Tuple2";
import { Placeholder } from "../../magma/app/compile/value/Placeholder";
import { TypeCompiler } from "../../magma/app/TypeCompiler";
import { Option } from "../../magma/api/option/Option";
import { Rule } from "../../magma/app/compile/rule/Rule";
import { Strings } from "../../magma/api/text/Strings";
import { PrefixComposable } from "../../magma/app/compile/compose/PrefixComposable";
import { SuffixComposable } from "../../magma/app/compile/compose/SuffixComposable";
import { Some } from "../../magma/api/option/Some";
import { StringValue } from "../../magma/app/compile/value/StringValue";
import { Not } from "../../magma/app/compile/value/Not";
import { LocatingSplitter } from "../../magma/app/compile/split/LocatingSplitter";
import { FirstLocator } from "../../magma/app/compile/locate/FirstLocator";
import { Splitter } from "../../magma/app/compile/split/Splitter";
import { SplitComposable } from "../../magma/app/compile/compose/SplitComposable";
import { Composable } from "../../magma/app/compile/compose/Composable";
import { DefiningCompiler } from "../../magma/app/DefiningCompiler";
import { Parameter } from "../../magma/app/compile/define/Parameter";
import { List } from "../../magma/api/collect/list/List";
import { Definition } from "../../magma/app/compile/define/Definition";
import { Iterable } from "../../magma/api/collect/list/Iterable";
import { FunctionSegmentCompiler } from "../../magma/app/FunctionSegmentCompiler";
import { Stack } from "../../magma/app/compile/Stack";
import { Lambda } from "../../magma/app/compile/value/Lambda";
import { Symbols } from "../../magma/app/compile/symbol/Symbols";
import { None } from "../../magma/api/option/None";
import { AccessValue } from "../../magma/app/compile/value/AccessValue";
import { FoldingSplitter } from "../../magma/app/compile/split/FoldingSplitter";
import { OperatorFolder } from "../../magma/app/compile/fold/OperatorFolder";
import { FirstSelector } from "../../magma/app/compile/select/FirstSelector";
import { Operation } from "../../magma/app/compile/value/Operation";
import { HeadedIter } from "../../magma/api/collect/head/HeadedIter";
import { RangeHead } from "../../magma/api/collect/head/RangeHead";
import { Characters } from "../../magma/api/text/Characters";
import { Type } from "../../magma/app/compile/type/Type";
import { Symbol } from "../../magma/app/compile/value/Symbol";
import { PrimitiveType } from "../../magma/app/compile/type/PrimitiveType";
import { Argument } from "../../magma/app/compile/value/Argument";
import { Caller } from "../../magma/app/compile/value/Caller";
import { DivideState } from "../../magma/app/compile/DivideState";
import { Iters } from "../../magma/api/collect/Iters";
import { ListCollector } from "../../magma/api/collect/list/ListCollector";
import { Invokable } from "../../magma/app/compile/value/Invokable";
import { OrRule } from "../../magma/app/compile/rule/OrRule";
import { Lists } from "../../jvm/api/collect/list/Lists";
import { DivideRule } from "../../magma/app/DivideRule";
import { ValueFolder } from "../../magma/app/compile/fold/ValueFolder";
import { Joiner } from "../../magma/api/collect/Joiner";
import { ConstructionCaller } from "../../magma/app/compile/value/ConstructionCaller";
export class ValueCompiler {
	static generateValue(tuple: Tuple2<CompileState, Value>): Tuple2Impl<CompileState, string> {
		let state = tuple.left()/*unknown*/;
		let right = tuple.right()/*unknown*/;
		let generated = ValueCompiler.generateCaller(right)/*unknown*/;
		let s = Placeholder.generatePlaceholder(TypeCompiler.generateType(ValueCompiler.resolve(state, right)))/*unknown*/;
		return new Tuple2Impl<CompileState, string>(state, generated + s)/*unknown*/;
	}
	static parseInvokable(state: CompileState, input: string): Option<Tuple2<CompileState, Value>> {
		/*return new SuffixComposable<Tuple2<CompileState, Value>>(")", (String withoutEnd) -> new SplitComposable<Tuple2<CompileState, Value>>((String withoutEnd0) -> {
            Selector selector */ = /* new LastSelector("");
            return new FoldingSplitter((DivideState state1, char c) -> ValueCompiler.foldInvocationStarts(state1, c), selector).apply(withoutEnd0);
        }, Composable.toComposable((String callerWithArgStart, String args) -> new SuffixComposable<Tuple2<CompileState, Value>>("(", (String callerString) -> new PrefixComposable<Tuple2<CompileState, Value>>("new ", (String type) -> TypeCompiler.createTypeRule().apply(state, type).<Tuple2<CompileState, String>>map((Tuple2<CompileState, Type> tuple) -> new Tuple2Impl<CompileState, String>(tuple.left(), TypeCompiler.generateType(tuple.right()))).flatMap((Tuple2<CompileState, String> callerTuple1) -> {
            var callerState = callerTuple1.right();
            var caller = callerTuple1.left();
            return ValueCompiler.assembleInvokable(caller, new ConstructionCaller(callerState), args);
        })).apply(Strings.strip(callerString)).or(() -> ValueCompiler.parseValue(state, callerString).flatMap((Tuple2<CompileState, Value> callerTuple) -> ValueCompiler.assembleInvokable(callerTuple.left(), callerTuple.right(), args)))).apply(callerWithArgStart))).apply(withoutEnd)).apply(Strings.strip(input))*/;
	}
	static createTextRule(slice: string): Rule<Value> {
		return (state1: CompileState, input1: string) => {
			let stripped = Strings.strip(input1)/*unknown*/;
			return new PrefixComposable<Tuple2<CompileState, Value>>(slice, (s: string) => new SuffixComposable<Tuple2<CompileState, Value>>(slice, (s1: string) => new Some<Tuple2<CompileState, Value>>(new Tuple2Impl<CompileState, Value>(state1, new StringValue(s1)))/*unknown*/).apply(s)/*unknown*/).apply(stripped)/*unknown*/;
		}/*unknown*/;
	}
	static parseNot(state: CompileState, input: string): Option<Tuple2<CompileState, Value>> {
		return new PrefixComposable<Tuple2<CompileState, Value>>("!", (withoutPrefix: string) => {
			let childTuple = ValueCompiler.compileValueOrPlaceholder(state, withoutPrefix)/*unknown*/;
			let childState = childTuple.left()/*unknown*/;
			let child = "!" + childTuple.right()/*unknown*/;
			return new Some<Tuple2<CompileState, Value>>(new Tuple2Impl<CompileState, Value>(childState, new Not(child)))/*unknown*/;
		}).apply(Strings.strip(input))/*unknown*/;
	}
	static parseLambda(state: CompileState, input: string): Option<Tuple2<CompileState, Value>> {
		let splitter: Splitter = new LocatingSplitter("->", new FirstLocator())/*unknown*/;
		return new SplitComposable<Tuple2<CompileState, Value>>(splitter, Composable.toComposable((beforeArrow: string, afterArrow: string) => {
			let strippedBeforeArrow = Strings.strip(beforeArrow)/*unknown*/;
			return new PrefixComposable<Tuple2<CompileState, Value>>("(", (withoutStart: string) => new SuffixComposable<Tuple2<CompileState, Value>>(")", (withoutEnd: string) => ValueCompiler.values((state1: CompileState, s: string) => DefiningCompiler.parseParameter(state1, s)/*unknown*/).apply(state, withoutEnd).flatMap((paramNames: Tuple2<CompileState, List<Parameter>>) => ValueCompiler.compileLambdaWithParameterNames(paramNames.left(), DefiningCompiler.retainDefinitionsFromParameters(paramNames.right()), afterArrow)/*unknown*/)/*unknown*/).apply(withoutStart)/*unknown*/).apply(strippedBeforeArrow)/*unknown*/;
		})).apply(input)/*unknown*/;
	}
	static compileLambdaWithParameterNames(state: CompileState, paramNames: Iterable<Definition>, afterArrow: string): Option<Tuple2<CompileState, Value>> {
		let strippedAfterArrow = Strings.strip(afterArrow)/*unknown*/;
		return new PrefixComposable<Tuple2<CompileState, Value>>("{", (withoutContentStart: string) => new SuffixComposable<Tuple2<CompileState, Value>>("}", (withoutContentEnd: string) => {
			let compileState: CompileState = state.enterDepth()/*unknown*/;
			let statementsTuple = FunctionSegmentCompiler.compileFunctionStatements(compileState.mapStack((stack1: Stack) => stack1.defineAll(paramNames)/*unknown*/), withoutContentEnd)/*unknown*/;
			let statementsState = statementsTuple.left()/*unknown*/;
			let statements = statementsTuple.right()/*unknown*/;
			let exited = statementsState.exitDepth()/*unknown*/;
			return ValueCompiler.assembleLambda(exited, paramNames, "{" + statements + exited.createIndent() + "}")/*unknown*/;
		}).apply(withoutContentStart)/*unknown*/).apply(strippedAfterArrow).or(() => ValueCompiler.compileValue(state, strippedAfterArrow).flatMap((tuple: Tuple2<CompileState, string>) => ValueCompiler.assembleLambda(tuple.left(), paramNames, tuple.right())/*unknown*/)/*unknown*/)/*unknown*/;
	}
	static assembleLambda(exited: CompileState, paramNames: Iterable<Definition>, content: string): Option<Tuple2<CompileState, Value>> {
		return new Some<Tuple2<CompileState, Value>>(new Tuple2Impl<CompileState, Value>(exited, new Lambda(paramNames, content)))/*unknown*/;
	}
	static createOperatorRule(infix: string): Rule<Value> {
		return ValueCompiler.createOperatorRuleWithDifferentInfix(infix, infix)/*unknown*/;
	}
	static createAccessRule(infix: string): Rule<Value> {
		return (state: CompileState, input: string) => SplitComposable.compileLast(input, infix, (childString: string, rawProperty: string) => {
			let property = Strings.strip(rawProperty)/*unknown*/;
			if (!!Symbols/*unknown*/.isSymbol(property)/*unknown*/){
				return new None<Tuple2<CompileState, Value>>()/*unknown*/;
			}
			return ValueCompiler.parseValue(state, childString).flatMap((childTuple: Tuple2<CompileState, Value>) => {
				let childState = childTuple.left()/*unknown*/;
				let child = childTuple.right()/*unknown*/;
				return new Some<Tuple2<CompileState, Value>>(new Tuple2Impl<CompileState, Value>(childState, new AccessValue(child, property)))/*unknown*/;
			})/*unknown*/;
		})/*unknown*//*unknown*/;
	}
	static createOperatorRuleWithDifferentInfix(sourceInfix: string, targetInfix: string): Rule<Value> {
		return (state1: CompileState, input1: string) => new SplitComposable<Tuple2<CompileState, Value>>((slice: string) => new FoldingSplitter(new OperatorFolder(sourceInfix), (divisions: List<string>) => new FirstSelector(sourceInfix).select(divisions)/*unknown*/).apply(slice)/*unknown*/, Composable.toComposable((leftString: string, rightString: string) => ValueCompiler.parseValue(state1, leftString).flatMap((leftTuple: Tuple2<CompileState, Value>) => ValueCompiler.parseValue(leftTuple.left(), rightString).flatMap((rightTuple: Tuple2<CompileState, Value>) => {
			let left = leftTuple.right()/*unknown*/;
			let right = rightTuple.right()/*unknown*/;
			return new Some<Tuple2<CompileState, Value>>(new Tuple2Impl<CompileState, Value>(rightTuple.left(), new Operation(left, targetInfix, right)))/*unknown*/;
		})/*unknown*/)/*unknown*/)).apply(input1)/*unknown*//*unknown*/;
	}
	static isNumber(input: string): boolean {
		let query = new HeadedIter<number>(new RangeHead(Strings.length(input)))/*unknown*/;
		return query.map(input.charAt).allMatch((c: string) => Characters.isDigit(c)/*unknown*/)/*unknown*/;
	}
	static resolve(state: CompileState, value: Value): Type {/*return switch (value) {
            case AccessValue accessValue -> PrimitiveType.Unknown;
            case Invokable invokable -> PrimitiveType.Unknown;
            case Lambda lambda -> PrimitiveType.Unknown;
            case Not not -> PrimitiveType.Boolean;
            case Operation operation -> PrimitiveType.Unknown;
            case Placeholder placeholder -> PrimitiveType.Unknown;
            case StringValue stringValue -> PrimitiveType.String;
            case Symbol symbol -> ValueCompiler.resolveSymbol(state, symbol);
            default -> PrimitiveType.Unknown;
        }*/;
	}
	static resolveSymbol(state: CompileState, symbol: Symbol): Type {
		return state.findStack().resolveValue(symbol.value()).map((definition: Definition) => definition.type()/*unknown*/).orElse(PrimitiveType.Unknown)/*unknown*/;
	}
	static parseNumber(state: CompileState, input: string): Option<Tuple2<CompileState, Value>> {
		let stripped = Strings.strip(input)/*unknown*/;
		if (ValueCompiler.isNumber(stripped)/*unknown*/){
			return new Some<Tuple2<CompileState, Value>>(new Tuple2Impl<CompileState, Value>(state, new Symbol(stripped)))/*unknown*/;
		}
		else {
			return new None<Tuple2<CompileState, Value>>()/*unknown*/;
		}
	}
	static compileValueOrPlaceholder(state: CompileState, input: string): Tuple2<CompileState, string> {
		return ValueCompiler.compileValue(state, input).orElseGet(() => new Tuple2Impl<CompileState, string>(state, Placeholder.generatePlaceholder(input))/*unknown*/)/*unknown*/;
	}
	static compileValue(state: CompileState, input: string): Option<Tuple2<CompileState, string>> {
		return ValueCompiler.parseValue(state, input).map((tuple: Tuple2<CompileState, Value>) => ValueCompiler.generateValue(tuple)/*unknown*/)/*unknown*/;
	}
	static parseArgument(state1: CompileState, input: string): Option<Tuple2<CompileState, Argument>> {
		return ValueCompiler.parseValue(state1, input).map((tuple: Tuple2<CompileState, Value>) => new Tuple2Impl<CompileState, Argument>(tuple.left(), tuple.right())/*unknown*/)/*unknown*/;
	}
	static transformCaller(state: CompileState, oldCaller: Caller): Caller {
		if (/*oldCaller instanceof AccessValue(var parent, _)*/){
			let parentType = ValueCompiler.resolve(state, parent)/*unknown*/;
			if (parentType.isFunctional()/*unknown*/){
				return parent/*unknown*/;
			}
		}
		return oldCaller/*Caller*/;
	}
	static foldInvocationStarts(state: DivideState, c: string): DivideState {
		let appended = state.append(c)/*unknown*/;
		if ("(" === c/*unknown*/){
			let entered = appended.enter()/*unknown*/;
			if (entered.isShallow()/*unknown*/){
				return entered.advance()/*unknown*/;
			}
			else {
				return entered/*unknown*/;
			}
		}
		if (")" === c/*unknown*/){
			return appended.exit()/*unknown*/;
		}
		return appended/*unknown*/;
	}
	static assembleInvokable(state: CompileState, oldCaller: Caller, argsString: string): Option<Tuple2<CompileState, Value>> {
		return ValueCompiler.values((state1: CompileState, s: string) => ValueCompiler.parseArgument(state1, s)/*unknown*/).apply(state, argsString).flatMap((argsTuple: Tuple2<CompileState, List<Argument>>) => {
			let argsState = argsTuple.left()/*unknown*/;
			let args = argsTuple.right().iter().map(ValueCompiler.retainValue).flatMap(Iters.fromOption).collect(new ListCollector<Value>())/*unknown*/;
			let newCaller = ValueCompiler.transformCaller(argsState, oldCaller)/*unknown*/;
			return new Some<Tuple2<CompileState, Value>>(new Tuple2Impl<CompileState, Value>(argsState, new Invokable(newCaller, args)))/*unknown*/;
		})/*unknown*/;
	}
	static retainValue(argument: Argument): Option<Value> {
		if (/*argument instanceof Value value*/){
			return new Some<Value>(value)/*unknown*/;
		}
		else {
			return new None<Value>()/*unknown*/;
		}
	}
	static parseValue(state: CompileState, input: string): Option<Tuple2<CompileState, Value>> {
		return new OrRule<Value>(Lists.of(ValueCompiler.parseLambda, ValueCompiler.createOperatorRule("+"), ValueCompiler.createOperatorRule("-"), ValueCompiler.createOperatorRule("<="), ValueCompiler.createOperatorRule("<"), ValueCompiler.createOperatorRule("&&"), ValueCompiler.createOperatorRule("||"), ValueCompiler.createOperatorRule(">"), ValueCompiler.createOperatorRule(">="), ValueCompiler.parseInvokable, ValueCompiler.createAccessRule("."), ValueCompiler.createAccessRule("::"), Symbols.parseSymbolValue, ValueCompiler.parseNot, ValueCompiler.parseNumber, ValueCompiler.createOperatorRuleWithDifferentInfix("==", "==="), ValueCompiler.createOperatorRuleWithDifferentInfix("!=", "!=="), ValueCompiler.createTextRule("\""), ValueCompiler.createTextRule("'"))).apply(state, input)/*unknown*/;
	}
	static values<T>(mapper: Rule<T>): Rule<List<T>> {
		return new DivideRule<T>(new ValueFolder(), mapper)/*unknown*/;
	}
	static generateValue(value: Value): string {/*return switch (value) {
            case AccessValue accessValue -> ValueCompiler.generateAccess(accessValue);
            case Invokable invokable -> ValueCompiler.generateInvokable(invokable);
            case Lambda lambda -> ValueCompiler.generateLambda(lambda);
            case Not not -> ValueCompiler.generateNot(not);
            case Operation operation -> ValueCompiler.generateOperation(operation);
            case Placeholder placeholder -> ValueCompiler.generatePlaceholder(placeholder);
            case StringValue stringValue -> ValueCompiler.generateStringValue(stringValue);
            case Symbol symbol -> ValueCompiler.generateSymbol(symbol);
            default -> "?";
        }*/;
	}
	static generateSymbol(symbol: Symbol): string {
		return symbol.value()/*unknown*/;
	}
	static generateStringValue(stringValue: StringValue): string {
		return "\"" + stringValue.value() + "\""/*unknown*/;
	}
	static generatePlaceholder(placeholder: Placeholder): string {
		return Placeholder.generatePlaceholder(placeholder.input())/*unknown*/;
	}
	static generateOperation(operation: Operation): string {
		return ValueCompiler.generateCaller(operation.left()) + " " + operation.targetInfix() + " " + ValueCompiler.generateCaller(operation.right())/*unknown*/;
	}
	static generateNot(not: Not): string {
		return "!" + not.child()/*unknown*/;
	}
	static generateLambda(lambda: Lambda): string {
		let joinedParamNames = lambda.parameters().iter().map((definition: Definition) => definition.generate()/*unknown*/).collect(new Joiner(", ")).orElse("")/*unknown*/;
		return "(" + joinedParamNames + ")" + " => " + lambda.content()/*unknown*/;
	}
	static generateInvokable(invokable: Invokable): string {
		let joinedArguments = ValueCompiler.joinArgs(invokable.args())/*unknown*/;
		return ValueCompiler.generateCaller(invokable.caller()) + "(" + joinedArguments + ")"/*unknown*/;
	}
	static generateAccess(accessValue: AccessValue): string {
		return ValueCompiler.generateCaller(accessValue.child()) + "." + accessValue.property()/*unknown*/;
	}
	static generateCaller(caller: Caller): string {/*return switch (caller) {
            case ConstructionCaller constructionCaller -> ValueCompiler.getGenerate(constructionCaller);
            case Value value -> ValueCompiler.generateValue(value);
            default -> "?";
        }*/;
	}
	static getGenerate(constructionCaller: ConstructionCaller): string {
		return "new " + constructionCaller.type()/*unknown*/;
	}
	static joinArgs(args1: Iterable<Value>): string {
		return args1.iter().map((value: Value) => ValueCompiler.generateCaller(value)/*unknown*/).collect(new Joiner(", ")).orElse("")/*unknown*/;
	}
}
