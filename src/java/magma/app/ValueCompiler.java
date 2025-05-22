package magma.app;

import jvm.api.collect.list.Lists;
import magma.api.Tuple2;
import magma.api.Tuple2Impl;
import magma.api.collect.Iters;
import magma.api.collect.Joiner;
import magma.api.collect.head.HeadedIter;
import magma.api.collect.head.RangeHead;
import magma.api.collect.list.Iterable;
import magma.api.collect.list.List;
import magma.api.collect.list.ListCollector;
import magma.api.option.None;
import magma.api.option.Option;
import magma.api.option.Some;
import magma.api.text.Characters;
import magma.api.text.Strings;
import magma.app.compile.CompileState;
import magma.app.compile.DivideState;
import magma.app.compile.Stack;
import magma.app.compile.compose.Composable;
import magma.app.compile.compose.PrefixComposable;
import magma.app.compile.compose.SplitComposable;
import magma.app.compile.compose.SuffixComposable;
import magma.app.compile.define.Definition;
import magma.app.compile.define.Parameter;
import magma.app.compile.fold.OperatorFolder;
import magma.app.compile.fold.ValueFolder;
import magma.app.compile.locate.FirstLocator;
import magma.app.compile.rule.OrRule;
import magma.app.compile.rule.Rule;
import magma.app.compile.select.FirstSelector;
import magma.app.compile.select.LastSelector;
import magma.app.compile.select.Selector;
import magma.app.compile.split.FoldingSplitter;
import magma.app.compile.split.LocatingSplitter;
import magma.app.compile.split.Splitter;
import magma.app.compile.symbol.Symbols;
import magma.app.compile.type.PrimitiveType;
import magma.app.compile.type.Type;
import magma.app.compile.value.AccessValue;
import magma.app.compile.value.Argument;
import magma.app.compile.value.Caller;
import magma.app.compile.value.ConstructionCaller;
import magma.app.compile.value.Invokable;
import magma.app.compile.value.Lambda;
import magma.app.compile.value.Not;
import magma.app.compile.value.Operation;
import magma.app.compile.value.Placeholder;
import magma.app.compile.value.StringValue;
import magma.app.compile.value.Symbol;
import magma.app.compile.value.Value;

public final class ValueCompiler {
    static Tuple2Impl<CompileState, String> generateValue(Tuple2<CompileState, Value> tuple) {
        var state = tuple.left();
        var right = tuple.right();
        var generated = ValueCompiler.generateCaller(right);
        var s = Placeholder.fromValue(TypeCompiler.generateType(ValueCompiler.resolve(state, right)));
        return new Tuple2Impl<CompileState, String>(state, generated + s);
    }

    static Option<Tuple2<CompileState, Value>> parseInvokable(CompileState state, String input) {
        return new SuffixComposable<Tuple2<CompileState, Value>>(")", (String withoutEnd) -> new SplitComposable<Tuple2<CompileState, Value>>((String withoutEnd0) -> {
            Selector selector = new LastSelector("");
            return new FoldingSplitter((DivideState state1, char c) -> ValueCompiler.foldInvocationStarts(state1, c), selector).apply(withoutEnd0);
        }, Composable.toComposable((String callerWithArgStart, String args) -> new SuffixComposable<Tuple2<CompileState, Value>>("(", (String callerString) -> new PrefixComposable<Tuple2<CompileState, Value>>("new ", (String type) -> TypeCompiler.createTypeRule().apply(state, type).<Tuple2<CompileState, String>>map((Tuple2<CompileState, Type> tuple) -> new Tuple2Impl<CompileState, String>(tuple.left(), TypeCompiler.generateType(tuple.right()))).flatMap((Tuple2<CompileState, String> callerTuple1) -> {
            var callerState = callerTuple1.right();
            var caller = callerTuple1.left();
            return ValueCompiler.assembleInvokable(caller, new ConstructionCaller(callerState), args);
        })).apply(Strings.strip(callerString)).or(() -> ValueCompiler.parseValue(state, callerString).flatMap((Tuple2<CompileState, Value> callerTuple) -> ValueCompiler.assembleInvokable(callerTuple.left(), callerTuple.right(), args)))).apply(callerWithArgStart))).apply(withoutEnd)).apply(Strings.strip(input));
    }

    private static Rule<Value> createTextRule(String slice) {
        return (CompileState state1, String input1) -> {
            var stripped = Strings.strip(input1);
            return new PrefixComposable<Tuple2<CompileState, Value>>(slice, (String s) -> new SuffixComposable<Tuple2<CompileState, Value>>(slice, (String s1) -> new Some<Tuple2<CompileState, Value>>(new Tuple2Impl<CompileState, Value>(state1, new StringValue(s1)))).apply(s)).apply(stripped);
        };
    }

    private static Option<Tuple2<CompileState, Value>> parseNot(CompileState state, String input) {
        return new PrefixComposable<Tuple2<CompileState, Value>>("!", (String withoutPrefix) -> {
            var childTuple = ValueCompiler.compileValueOrPlaceholder(state, withoutPrefix);
            var childState = childTuple.left();
            var child = "!" + childTuple.right();
            return new Some<Tuple2<CompileState, Value>>(new Tuple2Impl<CompileState, Value>(childState, new Not(child)));
        }).apply(Strings.strip(input));
    }

    private static Option<Tuple2<CompileState, Value>> parseLambda(CompileState state, String input) {
        Splitter splitter = new LocatingSplitter("->", new FirstLocator());
        return new SplitComposable<Tuple2<CompileState, Value>>(splitter, Composable.toComposable((String beforeArrow, String afterArrow) -> {
            var strippedBeforeArrow = Strings.strip(beforeArrow);
            return new PrefixComposable<Tuple2<CompileState, Value>>("(", (String withoutStart) -> new SuffixComposable<Tuple2<CompileState, Value>>(")", (String withoutEnd) -> ValueCompiler.values((CompileState state1, String s) -> DefiningCompiler.parseParameter(state1, s)).apply(state, withoutEnd).flatMap((Tuple2<CompileState, List<Parameter>> paramNames) -> ValueCompiler.compileLambdaWithParameterNames(paramNames.left(), DefiningCompiler.retainDefinitionsFromParameters(paramNames.right()), afterArrow))).apply(withoutStart)).apply(strippedBeforeArrow);
        })).apply(input);
    }

    private static Option<Tuple2<CompileState, Value>> compileLambdaWithParameterNames(CompileState state, Iterable<Definition> paramNames, String afterArrow) {
        var strippedAfterArrow = Strings.strip(afterArrow);
        return new PrefixComposable<Tuple2<CompileState, Value>>("{", (String withoutContentStart) -> new SuffixComposable<Tuple2<CompileState, Value>>("}", (String withoutContentEnd) -> {
            CompileState compileState = state.enterDepth();
            var statementsTuple = FunctionSegmentCompiler.compileFunctionStatements(compileState.mapStack((Stack stack1) -> stack1.defineAll(paramNames)), withoutContentEnd);
            var statementsState = statementsTuple.left();
            var statements = statementsTuple.right();

            var exited = statementsState.exitDepth();
            return ValueCompiler.assembleLambda(exited, paramNames, "{" + statements + exited.createIndent() + "}");
        }).apply(withoutContentStart)).apply(strippedAfterArrow).or(() -> ValueCompiler.compileValue(state, strippedAfterArrow).flatMap((Tuple2<CompileState, String> tuple) -> ValueCompiler.assembleLambda(tuple.left(), paramNames, tuple.right())));
    }

    private static Option<Tuple2<CompileState, Value>> assembleLambda(CompileState exited, Iterable<Definition> paramNames, String content) {
        return new Some<Tuple2<CompileState, Value>>(new Tuple2Impl<CompileState, Value>(exited, new Lambda(paramNames, content)));
    }

    private static Rule<Value> createOperatorRule(String infix) {
        return ValueCompiler.createOperatorRuleWithDifferentInfix(infix, infix);
    }

    private static Rule<Value> createAccessRule(String infix) {
        return (CompileState state, String input) -> SplitComposable.compileLast(input, infix, (String childString, String rawProperty) -> {
            var property = Strings.strip(rawProperty);
            if (!Symbols.isSymbol(property)) {
                return new None<Tuple2<CompileState, Value>>();
            }

            return ValueCompiler.parseValue(state, childString).flatMap((Tuple2<CompileState, Value> childTuple) -> {
                var childState = childTuple.left();
                var child = childTuple.right();
                return new Some<Tuple2<CompileState, Value>>(new Tuple2Impl<CompileState, Value>(childState, new AccessValue(child, property)));
            });
        });
    }

    private static Rule<Value> createOperatorRuleWithDifferentInfix(String sourceInfix, String targetInfix) {
        return (CompileState state1, String input1) -> new SplitComposable<Tuple2<CompileState, Value>>((String slice) -> new FoldingSplitter(new OperatorFolder(sourceInfix), (List<String> divisions) -> new FirstSelector(sourceInfix).select(divisions)).apply(slice), Composable.toComposable((String leftString, String rightString) -> ValueCompiler.parseValue(state1, leftString).flatMap((Tuple2<CompileState, Value> leftTuple) -> ValueCompiler.parseValue(leftTuple.left(), rightString).flatMap((Tuple2<CompileState, Value> rightTuple) -> {
            var left = leftTuple.right();
            var right = rightTuple.right();
            return new Some<Tuple2<CompileState, Value>>(new Tuple2Impl<CompileState, Value>(rightTuple.left(), new Operation(left, targetInfix, right)));
        })))).apply(input1);
    }

    private static boolean isNumber(String input) {
        var query = new HeadedIter<Integer>(new RangeHead(Strings.length(input)));
        return query.map(input::charAt).allMatch((Character c) -> Characters.isDigit(c));
    }

    private static Type resolve(CompileState state, Value value) {
        return switch (value) {
            case AccessValue accessValue -> PrimitiveType.Unknown;
            case Invokable invokable -> PrimitiveType.Unknown;
            case Lambda lambda -> PrimitiveType.Unknown;
            case Not not -> PrimitiveType.Boolean;
            case Operation operation -> PrimitiveType.Unknown;
            case Placeholder placeholder -> PrimitiveType.Unknown;
            case StringValue stringValue -> PrimitiveType.String;
            case Symbol symbol -> ValueCompiler.resolveSymbol(state, symbol);
            default -> PrimitiveType.Unknown;
        };
    }

    private static Type resolveSymbol(CompileState state, Symbol symbol) {
        return state.findStack().resolveValue(symbol.value())
                .map((Definition definition) -> definition.type())
                .orElse(PrimitiveType.Unknown);
    }

    private static Option<Tuple2<CompileState, Value>> parseNumber(CompileState state, String input) {
        var stripped = Strings.strip(input);
        if (ValueCompiler.isNumber(stripped)) {
            return new Some<Tuple2<CompileState, Value>>(new Tuple2Impl<CompileState, Value>(state, new Symbol(stripped)));
        }
        else {
            return new None<Tuple2<CompileState, Value>>();
        }
    }

    static Tuple2<CompileState, String> compileValueOrPlaceholder(CompileState state, String input) {
        return ValueCompiler.compileValue(state, input).orElseGet(() -> new Tuple2Impl<CompileState, String>(state, Placeholder.fromValue(input)));
    }

    static Option<Tuple2<CompileState, String>> compileValue(CompileState state, String input) {
        return ValueCompiler.parseValue(state, input).map((Tuple2<CompileState, Value> tuple) -> ValueCompiler.generateValue(tuple));
    }

    private static Option<Tuple2<CompileState, Argument>> parseArgument(CompileState state1, String input) {
        return ValueCompiler.parseValue(state1, input)
                .map((Tuple2<CompileState, Value> tuple) -> new Tuple2Impl<CompileState, Argument>(tuple.left(), tuple.right()));
    }

    private static Caller transformCaller(CompileState state, Caller oldCaller) {
        if (oldCaller instanceof AccessValue(var parent, _)) {
            var parentType = ValueCompiler.resolve(state, parent);
            if (parentType.isFunctional()) {
                return parent;
            }
        }

        return oldCaller;
    }

    private static DivideState foldInvocationStarts(DivideState state, char c) {
        var appended = state.append(c);
        if ('(' == c) {
            var entered = appended.enter();
            if (entered.isShallow()) {
                return entered.advance();
            }
            else {
                return entered;
            }
        }

        if (')' == c) {
            return appended.exit();
        }

        return appended;
    }

    private static Option<Tuple2<CompileState, Value>> assembleInvokable(CompileState state, Caller oldCaller, String argsString) {
        return ValueCompiler.values((CompileState state1, String s) -> ValueCompiler.parseArgument(state1, s)).apply(state, argsString).flatMap((Tuple2<CompileState, List<Argument>> argsTuple) -> {
            var argsState = argsTuple.left();
            var args = argsTuple.right()
                    .iter()
                    .map(ValueCompiler::retainValue)
                    .flatMap(Iters::fromOption)
                    .collect(new ListCollector<Value>());

            var newCaller = ValueCompiler.transformCaller(argsState, oldCaller);
            return new Some<Tuple2<CompileState, Value>>(new Tuple2Impl<CompileState, Value>(argsState, new Invokable(newCaller, args)));
        });
    }

    private static Option<Value> retainValue(Argument argument) {
        if (argument instanceof Value value) {
            return new Some<Value>(value);
        }
        else {
            return new None<Value>();
        }
    }

    private static Option<Tuple2<CompileState, Value>> parseValue(CompileState state, String input) {
        return new OrRule<Value>(Lists.of(
                ValueCompiler::parseLambda,
                ValueCompiler.createOperatorRule("+"),
                ValueCompiler.createOperatorRule("-"),
                ValueCompiler.createOperatorRule("<="),
                ValueCompiler.createOperatorRule("<"),
                ValueCompiler.createOperatorRule("&&"),
                ValueCompiler.createOperatorRule("||"),
                ValueCompiler.createOperatorRule(">"),
                ValueCompiler.createOperatorRule(">="),
                ValueCompiler::parseInvokable,
                ValueCompiler.createAccessRule("."),
                ValueCompiler.createAccessRule("::"),
                Symbols::parseSymbolValue,
                ValueCompiler::parseNot,
                ValueCompiler::parseNumber,
                ValueCompiler.createOperatorRuleWithDifferentInfix("==", "==="),
                ValueCompiler.createOperatorRuleWithDifferentInfix("!=", "!=="),
                ValueCompiler.createTextRule("\""),
                ValueCompiler.createTextRule("'")
        )).apply(state, input);
    }

    public static <T> Rule<List<T>> values(Rule<T> mapper) {
        return new DivideRule<T>(new ValueFolder(), mapper);
    }

    public static String generateValue(Value value) {
        return switch (value) {
            case AccessValue accessValue -> ValueCompiler.generateAccess(accessValue);
            case Invokable invokable -> ValueCompiler.generateInvokable(invokable);
            case Lambda lambda -> ValueCompiler.generateLambda(lambda);
            case Not not -> ValueCompiler.generateNot(not);
            case Operation operation -> ValueCompiler.generateOperation(operation);
            case Placeholder placeholder -> ValueCompiler.generatePlaceholder(placeholder);
            case StringValue stringValue -> ValueCompiler.generateStringValue(stringValue);
            case Symbol symbol -> ValueCompiler.generateSymbol(symbol);
            default -> "?";
        };
    }

    private static String generateSymbol(Symbol symbol) {
        return symbol.value();
    }

    private static String generateStringValue(StringValue stringValue) {
        return "\"" + stringValue.value() + "\"";
    }

    private static String generatePlaceholder(Placeholder placeholder) {
        return Placeholder.fromValue(placeholder.value());
    }

    private static String generateOperation(Operation operation) {
        return ValueCompiler.generateCaller(operation.left()) + " " + operation.targetInfix() + " " + ValueCompiler.generateCaller(operation.right());
    }

    private static String generateNot(Not not) {
        return "!" + not.child();
    }

    private static String generateLambda(Lambda lambda) {
        var joinedParamNames = lambda.parameters()
                .iter()
                .map((Definition definition) -> definition.generate())
                .collect(new Joiner(", "))
                .orElse("");

        return "(" + joinedParamNames + ")" + " => " + lambda.content();
    }

    private static String generateInvokable(Invokable invokable) {
        var joinedArguments = ValueCompiler.joinArgs(invokable.args());
        return ValueCompiler.generateCaller(invokable.caller()) + "(" + joinedArguments + ")";
    }

    private static String generateAccess(AccessValue accessValue) {
        return ValueCompiler.generateCaller(accessValue.child()) + "." + accessValue.property();
    }

    public static String generateCaller(Caller caller) {
        return switch (caller) {
            case ConstructionCaller constructionCaller -> ValueCompiler.getGenerate(constructionCaller);
            case Value value -> ValueCompiler.generateValue(value);
            default -> "?";
        };
    }

    private static String getGenerate(ConstructionCaller constructionCaller) {
        return "new " + constructionCaller.type();
    }

    public static String joinArgs(Iterable<Value> args1) {
        return args1.iter()
                .map((Value value) -> ValueCompiler.generateCaller(value))
                .collect(new Joiner(", "))
                .orElse("");
    }
}