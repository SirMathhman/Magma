import { CompileState } from "../../magma/app/compile/CompileState";
import { Tuple2Impl } from "../../magma/api/Tuple2Impl";
import { Node } from "../../magma/app/compile/node/Node";
import { Tuple2 } from "../../magma/api/Tuple2";
import { Placeholder } from "../../magma/app/compile/value/Placeholder";
import { Option } from "../../magma/api/option/Option";
import { SuffixComposable } from "../../magma/app/compile/compose/SuffixComposable";
import { SplitComposable } from "../../magma/app/compile/compose/SplitComposable";
import { LastSelector } from "../../magma/app/compile/select/LastSelector";
import { Selector } from "../../magma/app/compile/select/Selector";
import { FoldingSplitter } from "../../magma/app/compile/split/FoldingSplitter";
import { DivideState } from "../../magma/app/compile/DivideState";
import { Composable } from "../../magma/app/compile/compose/Composable";
import { PrefixComposable } from "../../magma/app/compile/compose/PrefixComposable";
import { TypeCompiler } from "../../magma/app/TypeCompiler";
import { ConstructionCaller } from "../../magma/app/compile/value/ConstructionCaller";
import { Strings } from "../../magma/api/text/Strings";
import { Rule } from "../../magma/app/compile/rule/Rule";
import { Some } from "../../magma/api/option/Some";
import { StringNode } from "../../magma/app/compile/value/StringNode";
import { Not } from "../../magma/app/compile/value/Not";
import { LocatingSplitter } from "../../magma/app/compile/split/LocatingSplitter";
import { FirstLocator } from "../../magma/app/compile/locate/FirstLocator";
import { Splitter } from "../../magma/app/compile/split/Splitter";
import { DefiningCompiler } from "../../magma/app/DefiningCompiler";
import { Parameter } from "../../magma/app/compile/define/Parameter";
import { List } from "../../magma/api/collect/list/List";
import { Definition } from "../../magma/app/compile/define/Definition";
import { Iterable } from "../../magma/api/collect/list/Iterable";
import { FunctionSegmentCompiler } from "../../magma/app/FunctionSegmentCompiler";
import { Stack } from "../../magma/app/compile/Stack";
import { Lambda } from "../../magma/app/compile/value/Lambda";
import { None } from "../../magma/api/option/None";
import { Access } from "../../magma/app/compile/value/Access";
import { OperatorFolder } from "../../magma/app/compile/fold/OperatorFolder";
import { FirstSelector } from "../../magma/app/compile/select/FirstSelector";
import { Operation } from "../../magma/app/compile/value/Operation";
import { Symbol } from "../../magma/app/compile/value/Symbol";
import { HeadedIter } from "../../magma/api/collect/head/HeadedIter";
import { RangeHead } from "../../magma/api/collect/head/RangeHead";
import { Characters } from "../../magma/api/text/Characters";
import { Type } from "../../magma/app/compile/type/Type";
import { Invokable } from "../../magma/app/compile/value/Invokable";
import { Iters } from "../../magma/api/collect/Iters";
import { ListCollector } from "../../magma/api/collect/list/ListCollector";
import { OrRule } from "../../magma/app/compile/rule/OrRule";
import { Lists } from "../../jvm/api/collect/list/Lists";
import { DivideRule } from "../../magma/app/DivideRule";
import { ValueFolder } from "../../magma/app/compile/fold/ValueFolder";
export class ValueCompiler {
	static generateNode(tuple: Tuple2<CompileState, Node>): Tuple2Impl<CompileState, string> {
		let state = tuple.left()/*unknown*/;
		let right = tuple.right()/*unknown*/;
		let generated = ValueCompiler.generateValue(right)/*unknown*/;
		let s = Placeholder.generatePlaceholder(ValueCompiler.resolve(state, right).generate())/*unknown*/;
		return new Tuple2Impl<CompileState, string>(state, generated + s)/*unknown*/;
	}
	static parseInvokable(state: CompileState, input: string): Option<Tuple2<CompileState, Node>> {
		return new SuffixComposable<Tuple2<CompileState, Node>>(")", (withoutEnd: string) => {
			return new SplitComposable<Tuple2<CompileState, Node>>((withoutEnd0: string) => {
				let selector: Selector = new LastSelector("")/*unknown*/;
				return new FoldingSplitter((state1: DivideState, c: string) => {
					return ValueCompiler.foldInvocationStarts(state1, c)/*unknown*/;
				}, selector).apply(withoutEnd0)/*unknown*/;
			}, Composable.toComposable((callerWithArgStart: string, args: string) => {
				return new SuffixComposable<Tuple2<CompileState, Node>>("(", (callerString: string) => {
					return new PrefixComposable<Tuple2<CompileState, Node>>("new ", (type: string) => {
						return TypeCompiler.compileType(state, type).flatMap((callerTuple1: Tuple2<CompileState, string>) => {
							let callerState = callerTuple1.right()/*unknown*/;
							let caller = callerTuple1.left()/*unknown*/;
							return ValueCompiler.assembleInvokable(caller, new ConstructionCaller(callerState), args)/*unknown*/;
						})/*unknown*/;
					}).apply(Strings.strip(callerString)).or(() => {
						return ValueCompiler.parseNode(state, callerString).flatMap((callerTuple: Tuple2<CompileState, Node>) => {
							return ValueCompiler.assembleInvokable(callerTuple.left(), callerTuple.right(), args)/*unknown*/;
						})/*unknown*/;
					})/*unknown*/;
				}).apply(callerWithArgStart)/*unknown*/;
			})).apply(withoutEnd)/*unknown*/;
		}).apply(Strings.strip(input))/*unknown*/;
	}
	static createTextRule(slice: string): Rule<Node> {
		return (state1: CompileState, input1: string) => {
			let stripped = Strings.strip(input1)/*unknown*/;
			return new PrefixComposable<Tuple2<CompileState, Node>>(slice, (s: string) => {
				return new SuffixComposable<Tuple2<CompileState, Node>>(slice, (s1: string) => {
					return new Some<Tuple2<CompileState, Node>>(new Tuple2Impl<CompileState, Node>(state1, new StringNode(s1)))/*unknown*/;
				}).apply(s)/*unknown*/;
			}).apply(stripped)/*unknown*/;
		}/*unknown*/;
	}
	static parseNot(state: CompileState, input: string): Option<Tuple2<CompileState, Node>> {
		return new PrefixComposable<Tuple2<CompileState, Node>>("!", (withoutPrefix: string) => {
			let childTuple = ValueCompiler.compileNodeOrPlaceholder(state, withoutPrefix)/*unknown*/;
			let childState = childTuple.left()/*unknown*/;
			let child = "!" + childTuple.right()/*unknown*/;
			return new Some<Tuple2<CompileState, Node>>(new Tuple2Impl<CompileState, Node>(childState, new Not(child)))/*unknown*/;
		}).apply(Strings.strip(input))/*unknown*/;
	}
	static parseLambda(state: CompileState, input: string): Option<Tuple2<CompileState, Node>> {
		let splitter: Splitter = new LocatingSplitter("->", new FirstLocator())/*unknown*/;
		return new SplitComposable<Tuple2<CompileState, Node>>(splitter, Composable.toComposable((beforeArrow: string, afterArrow: string) => {
			let strippedBeforeArrow = Strings.strip(beforeArrow)/*unknown*/;
			return new PrefixComposable<Tuple2<CompileState, Node>>("(", (withoutStart: string) => {
				return new SuffixComposable<Tuple2<CompileState, Node>>(")", (withoutEnd: string) => {
					return ValueCompiler.values((state1: CompileState, s: string) => {
						return DefiningCompiler.parseParameter(state1, s)/*unknown*/;
					}).apply(state, withoutEnd).flatMap((paramNames: Tuple2<CompileState, List<Parameter>>) => {
						return ValueCompiler.compileLambdaWithParameterNames(paramNames.left(), DefiningCompiler.retainDefinitionsFromParameters(paramNames.right()), afterArrow)/*unknown*/;
					})/*unknown*/;
				}).apply(withoutStart)/*unknown*/;
			}).apply(strippedBeforeArrow)/*unknown*/;
		})).apply(input)/*unknown*/;
	}
	static compileLambdaWithParameterNames(state: CompileState, paramNames: Iterable<Definition>, afterArrow: string): Option<Tuple2<CompileState, Node>> {
		let strippedAfterArrow = Strings.strip(afterArrow)/*unknown*/;
		return new PrefixComposable<Tuple2<CompileState, Node>>("{", (withoutContentStart: string) => {
			return new SuffixComposable<Tuple2<CompileState, Node>>("}", (withoutContentEnd: string) => {
				let compileState: CompileState = state.enterDepth()/*unknown*/;
				let statementsTuple = FunctionSegmentCompiler.compileFunctionStatements(compileState.mapStack((stack1: Stack) => {
					return stack1.defineAll(paramNames)/*unknown*/;
				}), withoutContentEnd)/*unknown*/;
				let statementsState = statementsTuple.left()/*unknown*/;
				let statements = statementsTuple.right()/*unknown*/;
				let exited = statementsState.exitDepth()/*unknown*/;
				return ValueCompiler.assembleLambda(exited, paramNames, "{" + statements + exited.createIndent() + "}")/*unknown*/;
			}).apply(withoutContentStart)/*unknown*/;
		}).apply(strippedAfterArrow).or(() => {
			return ValueCompiler.compileNode(state, strippedAfterArrow).flatMap((tuple: Tuple2<CompileState, string>) => {
				return ValueCompiler.assembleLambda(tuple.left(), paramNames, tuple.right())/*unknown*/;
			})/*unknown*/;
		})/*unknown*/;
	}
	static assembleLambda(exited: CompileState, paramNames: Iterable<Definition>, content: string): Option<Tuple2<CompileState, Node>> {
		return new Some<Tuple2<CompileState, Node>>(new Tuple2Impl<CompileState, Node>(exited, new Lambda(paramNames, content)))/*unknown*/;
	}
	static createOperatorRule(infix: string): Rule<Node> {
		return ValueCompiler.createOperatorRuleWithDifferentInfix(infix, infix)/*unknown*/;
	}
	static createAccessRule(infix: string): Rule<Node> {
		return (state: CompileState, input: string) => {
			return SplitComposable.compileLast(input, infix, (childString: string, rawProperty: string) => {
				let property = Strings.strip(rawProperty)/*unknown*/;
				if (!ValueCompiler/*unknown*/.isSymbol(property)/*unknown*/){
					return new None<Tuple2<CompileState, Node>>()/*unknown*/;
				}
				return ValueCompiler.parseNode(state, childString).flatMap((childTuple: Tuple2<CompileState, Node>) => {
					let childState = childTuple.left()/*unknown*/;
					let child = childTuple.right()/*unknown*/;
					return new Some<Tuple2<CompileState, Node>>(new Tuple2Impl<CompileState, Node>(childState, new Access(child, property)))/*unknown*/;
				})/*unknown*/;
			})/*unknown*/;
		}/*unknown*/;
	}
	static createOperatorRuleWithDifferentInfix(sourceInfix: string, targetInfix: string): Rule<Node> {
		return (state1: CompileState, input1: string) => {
			return new SplitComposable<Tuple2<CompileState, Node>>((slice: string) => {
				return new FoldingSplitter(new OperatorFolder(sourceInfix), (divisions: List<string>) => {
					return new FirstSelector(sourceInfix).select(divisions)/*unknown*/;
				}).apply(slice)/*unknown*/;
			}, Composable.toComposable((leftString: string, rightString: string) => {
				return ValueCompiler.parseNode(state1, leftString).flatMap((leftTuple: Tuple2<CompileState, Node>) => {
					return ValueCompiler.parseNode(leftTuple.left(), rightString).flatMap((rightTuple: Tuple2<CompileState, Node>) => {
						let left = leftTuple.right()/*unknown*/;
						let right = rightTuple.right()/*unknown*/;
						return new Some<Tuple2<CompileState, Node>>(new Tuple2Impl<CompileState, Node>(rightTuple.left(), new Operation(left, targetInfix, right)))/*unknown*/;
					})/*unknown*/;
				})/*unknown*/;
			})).apply(input1)/*unknown*/;
		}/*unknown*/;
	}
	static parseSymbol(state: CompileState, input: string): Option<Tuple2<CompileState, Node>> {
		let stripped = Strings.strip(input)/*unknown*/;
		if (ValueCompiler.isSymbol(stripped)/*unknown*/){
			let withImport = TypeCompiler.addResolvedImportFromCache0(state, stripped)/*unknown*/;
			return new Some<Tuple2<CompileState, Node>>(new Tuple2Impl<CompileState, Node>(withImport, new Symbol(stripped)))/*unknown*/;
		}
		else {
			return new None<Tuple2<CompileState, Node>>()/*unknown*/;
		}
	}
	static isSymbol(input: string): boolean {
		let query = new HeadedIter<number>(new RangeHead(Strings.length(input)))/*unknown*/;
		return query.allMatch((index: number) => {
			return ValueCompiler.isSymbolChar(index, input.charAt(index))/*unknown*/;
		})/*unknown*/;
	}
	static isSymbolChar(index: number, c: string): boolean {
		return "_" === c || Characters.isLetter(c) || (0 !== index && Characters.isDigit(c))/*unknown*/;
	}
	static isNumber(input: string): boolean {
		let query = new HeadedIter<number>(new RangeHead(Strings.length(input)))/*unknown*/;
		return query.map(input.charAt).allMatch((c: string) => {
			return Characters.isDigit(c)/*unknown*/;
		})/*unknown*/;
	}
	static resolve(state: CompileState, value: Node): Type {/*return switch (value) {
            case Access access -> access.resolve(state);
            case Invokable invokable -> invokable.resolve(state);
            case Lambda lambda -> lambda.resolve(state);
            case Not not -> not.resolve(state);
            case Operation operation -> operation.resolve(state);
            case Placeholder placeholder -> placeholder.resolve(state);
            case StringNode stringNode -> stringNode.resolve(state);
            case Symbol symbol -> symbol.resolve(state);
            default -> throw new IllegalStateException("Unexpected value: " + value);
        }*/;
	}
	static parseNumber(state: CompileState, input: string): Option<Tuple2<CompileState, Node>> {
		let stripped = Strings.strip(input)/*unknown*/;
		if (ValueCompiler.isNumber(stripped)/*unknown*/){
			return new Some<Tuple2<CompileState, Node>>(new Tuple2Impl<CompileState, Node>(state, new Symbol(stripped)))/*unknown*/;
		}
		else {
			return new None<Tuple2<CompileState, Node>>()/*unknown*/;
		}
	}
	static compileNodeOrPlaceholder(state: CompileState, input: string): Tuple2<CompileState, string> {
		return ValueCompiler.compileNode(state, input).orElseGet(() => {
			return new Tuple2Impl<CompileState, string>(state, Placeholder.generatePlaceholder(input))/*unknown*/;
		})/*unknown*/;
	}
	static compileNode(state: CompileState, input: string): Option<Tuple2<CompileState, string>> {
		return ValueCompiler.parseNode(state, input).map((tuple: Tuple2<CompileState, Node>) => {
			return ValueCompiler.generateNode(tuple)/*unknown*/;
		})/*unknown*/;
	}
	static parseArgument(state1: CompileState, input: string): Option<Tuple2<CompileState, Node>> {
		return ValueCompiler.parseNode(state1, input).map((tuple: Tuple2<CompileState, Node>) => {
			return new Tuple2Impl<CompileState, Node>(tuple.left(), tuple.right())/*unknown*/;
		})/*unknown*/;
	}
	static transformCaller(state: CompileState, oldNode: Node): Node {
		return ValueCompiler.getNodeOption(oldNode).flatMap((parent: Node) => {
			let parentType = ValueCompiler.resolve(state, parent)/*unknown*/;
			if (parentType.isFunctional()/*unknown*/){
				return new Some<Node>(parent)/*unknown*/;
			}
			return new None<Node>()/*unknown*/;
		}).orElse(oldNode)/*unknown*/;
	}
	static getNodeOption(oldNode: Node): Option<Node> {
		if (/*oldNode instanceof Access access*/){
			return new Some<Node>(access.child())/*unknown*/;
		}
		return new None<Node>()/*unknown*/;
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
	static assembleInvokable(state: CompileState, oldNode: Node, argsString: string): Option<Tuple2<CompileState, Node>> {
		return ValueCompiler.values((state1: CompileState, s: string) => {
			return ValueCompiler.parseArgument(state1, s)/*unknown*/;
		}).apply(state, argsString).flatMap((argsTuple: Tuple2<CompileState, List<Node>>) => {
			let argsState = argsTuple.left()/*unknown*/;
			let args = argsTuple.right()/*unknown*/;
			let newCaller = ValueCompiler.transformCaller(argsState, oldNode)/*unknown*/;
			return new Some<Tuple2<CompileState, Node>>(new Tuple2Impl<CompileState, Node>(argsState, new Invokable(newCaller, args)))/*unknown*/;
		})/*unknown*/;
	}
	static retain<T, R>(args: Iterable<T>, mapper: (arg0 : T) => Option<R>): Iterable<R> {
		return args.iter().map(mapper).flatMap(Iters.fromOption).collect(new ListCollector<R>())/*unknown*/;
	}
	static parseNode(state: CompileState, input: string): Option<Tuple2<CompileState, Node>> {
		return new OrRule<Node>(Lists.of(ValueCompiler.parseLambda, ValueCompiler.createOperatorRule("+"), ValueCompiler.createOperatorRule("-"), ValueCompiler.createOperatorRule("<="), ValueCompiler.createOperatorRule("<"), ValueCompiler.createOperatorRule("&&"), ValueCompiler.createOperatorRule("||"), ValueCompiler.createOperatorRule(">"), ValueCompiler.createOperatorRule(">="), ValueCompiler.parseInvokable, ValueCompiler.createAccessRule("."), ValueCompiler.createAccessRule("::"), ValueCompiler.parseSymbol, ValueCompiler.parseNot, ValueCompiler.parseNumber, ValueCompiler.createOperatorRuleWithDifferentInfix("==", "==="), ValueCompiler.createOperatorRuleWithDifferentInfix("!=", "!=="), ValueCompiler.createTextRule("\""), ValueCompiler.createTextRule("'"))).apply(state, input)/*unknown*/;
	}
	static values<T>(mapper: Rule<T>): Rule<List<T>> {
		return new DivideRule<>(new ValueFolder(), mapper)/*unknown*/;
	}
	static getString(node: Node): string {
		if (node.is("construction")/*unknown*/){
			let casted: ConstructionCaller = (ConstructionCaller)(node)/*unknown*/;
			return "new " + casted.type()/*unknown*/;
		}
		return ValueCompiler.generateValue(node)/*unknown*/;
	}
	static generateValue(value: Node): string {/*return switch (value) {
            case Access access -> access.generate();
            case Invokable invokable -> invokable.generate();
            case Lambda lambda -> lambda.generate();
            case Not not -> not.generate();
            case Operation operation -> operation.generate();
            case Placeholder placeholder -> placeholder.generate();
            case StringNode stringNode -> stringNode.generate();
            case Symbol symbol -> symbol.generate();
            default -> "?";
        }*/;
	}
}
