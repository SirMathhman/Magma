package magma.app;

import jvm.api.collect.list.Lists;
import magma.api.Tuple2;
import magma.api.Tuple2Impl;
import magma.api.collect.Iters;
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
import magma.app.compile.node.MapNode;
import magma.app.compile.node.Node;
import magma.app.compile.rule.OrRule;
import magma.app.compile.rule.Rule;
import magma.app.compile.select.FirstSelector;
import magma.app.compile.select.LastSelector;
import magma.app.compile.select.Selector;
import magma.app.compile.split.FoldingSplitter;
import magma.app.compile.split.LocatingSplitter;
import magma.app.compile.split.Splitter;
import magma.app.compile.type.PrimitiveType;
import magma.app.compile.type.Type;
import magma.app.compile.value.ConstructionCaller;
import magma.app.compile.value.Invokable;
import magma.app.compile.value.Lambda;
import magma.app.compile.value.Not;
import magma.app.compile.value.Operation;
import magma.app.compile.value.Placeholder;
import magma.app.compile.value.StringNode;
import magma.app.compile.value.Symbol;

import java.util.function.Function;

public final class ValueCompiler {
    static Tuple2Impl<CompileState, String> generateNode(Tuple2<CompileState, Node> tuple) {
        var state = tuple.left();
        var right = tuple.right();
        var generated = ValueCompiler.generateValue(right);
        var s = Placeholder.generatePlaceholder(ValueCompiler.resolve(state, right).generate());
        return new Tuple2Impl<CompileState, String>(state, generated + s);
    }

    static Option<Tuple2<CompileState, Node>> parseInvokable(CompileState state, String input) {
        return new SuffixComposable<Tuple2<CompileState, Node>>(")", (String withoutEnd) -> {
            return new SplitComposable<Tuple2<CompileState, Node>>((String withoutEnd0) -> {
                Selector selector = new LastSelector("");
                return new FoldingSplitter((DivideState state1, char c) -> {
                    return ValueCompiler.foldInvocationStarts(state1, c);
                }, selector).apply(withoutEnd0);
            }, Composable.toComposable((String callerWithArgStart, String args) -> {
                return new SuffixComposable<Tuple2<CompileState, Node>>("(", (String callerString) -> {
                    return new PrefixComposable<Tuple2<CompileState, Node>>("new ", (String type) -> {
                        return TypeCompiler.compileType(state, type).flatMap((Tuple2<CompileState, String> callerTuple1) -> {
                            var callerState = callerTuple1.right();
                            var caller = callerTuple1.left();
                            return ValueCompiler.assembleInvokable(caller, new ConstructionCaller(callerState), args);
                        });
                    }).apply(Strings.strip(callerString)).or(() -> {
                        return ValueCompiler.parseNode(state, callerString).flatMap((Tuple2<CompileState, Node> callerTuple) -> {
                            return ValueCompiler.assembleInvokable(callerTuple.left(), callerTuple.right(), args);
                        });
                    });
                }).apply(callerWithArgStart);
            })).apply(withoutEnd);
        }).apply(Strings.strip(input));
    }

    static Rule<Node> createTextRule(String slice) {
        return (CompileState state1, String input1) -> {
            var stripped = Strings.strip(input1);
            return new PrefixComposable<Tuple2<CompileState, Node>>(slice, (String s) -> {
                return new SuffixComposable<Tuple2<CompileState, Node>>(slice, (String s1) -> {
                    return new Some<Tuple2<CompileState, Node>>(new Tuple2Impl<CompileState, Node>(state1, new StringNode(s1)));
                }).apply(s);
            }).apply(stripped);
        };
    }

    static Option<Tuple2<CompileState, Node>> parseNot(CompileState state, String input) {
        return new PrefixComposable<Tuple2<CompileState, Node>>("!", (String withoutPrefix) -> {
            var childTuple = ValueCompiler.compileNodeOrPlaceholder(state, withoutPrefix);
            var childState = childTuple.left();
            var child = "!" + childTuple.right();
            return new Some<Tuple2<CompileState, Node>>(new Tuple2Impl<CompileState, Node>(childState, new Not(child)));
        }).apply(Strings.strip(input));
    }

    static Option<Tuple2<CompileState, Node>> parseLambda(CompileState state, String input) {
        Splitter splitter = new LocatingSplitter("->", new FirstLocator());
        return new SplitComposable<Tuple2<CompileState, Node>>(splitter, Composable.toComposable((String beforeArrow, String afterArrow) -> {
            var strippedBeforeArrow = Strings.strip(beforeArrow);
            return new PrefixComposable<Tuple2<CompileState, Node>>("(", (String withoutStart) -> {
                return new SuffixComposable<Tuple2<CompileState, Node>>(")", (String withoutEnd) -> {
                    return ValueCompiler.values((CompileState state1, String s) -> {
                        return DefiningCompiler.parseParameter(state1, s);
                    }).apply(state, withoutEnd).flatMap((Tuple2<CompileState, List<Parameter>> paramNames) -> {
                        return ValueCompiler.compileLambdaWithParameterNames(paramNames.left(), DefiningCompiler.retainDefinitionsFromParameters(paramNames.right()), afterArrow);
                    });
                }).apply(withoutStart);
            }).apply(strippedBeforeArrow);
        })).apply(input);
    }

    private static Option<Tuple2<CompileState, Node>> compileLambdaWithParameterNames(CompileState state, Iterable<Definition> paramNames, String afterArrow) {
        var strippedAfterArrow = Strings.strip(afterArrow);
        return new PrefixComposable<Tuple2<CompileState, Node>>("{", (String withoutContentStart) -> {
            return new SuffixComposable<Tuple2<CompileState, Node>>("}", (String withoutContentEnd) -> {
                CompileState compileState = state.enterDepth();
                var statementsTuple = FunctionSegmentCompiler.compileFunctionStatements(compileState.mapStack((Stack stack1) -> {
                    return stack1.defineAll(paramNames);
                }), withoutContentEnd);
                var statementsState = statementsTuple.left();
                var statements = statementsTuple.right();

                var exited = statementsState.exitDepth();
                return ValueCompiler.assembleLambda(exited, paramNames, "{" + statements + exited.createIndent() + "}");
            }).apply(withoutContentStart);
        }).apply(strippedAfterArrow).or(() -> {
            return ValueCompiler.compileNode(state, strippedAfterArrow).flatMap((Tuple2<CompileState, String> tuple) -> {
                return ValueCompiler.assembleLambda(tuple.left(), paramNames, tuple.right());
            });
        });
    }

    private static Option<Tuple2<CompileState, Node>> assembleLambda(CompileState exited, Iterable<Definition> paramNames, String content) {
        return new Some<Tuple2<CompileState, Node>>(new Tuple2Impl<CompileState, Node>(exited, new Lambda(paramNames, content)));
    }

    static Rule<Node> createOperatorRule(String infix) {
        return ValueCompiler.createOperatorRuleWithDifferentInfix(infix, infix);
    }

    static Rule<Node> createAccessRule(String infix) {
        return (CompileState state, String input) -> {
            return SplitComposable.compileLast(input, infix, (String childString, String rawProperty) -> {
                var property = Strings.strip(rawProperty);
                if (!ValueCompiler.isSymbol(property)) {
                    return new None<Tuple2<CompileState, Node>>();
                }

                return ValueCompiler.parseNode(state, childString).flatMap((Tuple2<CompileState, Node> childTuple) -> {
                    var childState = childTuple.left();
                    var child = childTuple.right();
                    return new Some<Tuple2<CompileState, Node>>(new Tuple2Impl<CompileState, Node>(childState, new MapNode("access")
                            .withNode("child", child)
                            .withString("property", property)));
                });
            });
        };
    }

    static Rule<Node> createOperatorRuleWithDifferentInfix(String sourceInfix, String targetInfix) {
        return (CompileState state1, String input1) -> {
            return new SplitComposable<Tuple2<CompileState, Node>>((String slice) -> {
                return new FoldingSplitter(new OperatorFolder(sourceInfix), (List<String> divisions) -> {
                    return new FirstSelector(sourceInfix).select(divisions);
                }).apply(slice);
            }, Composable.toComposable((String leftString, String rightString) -> {
                return ValueCompiler.parseNode(state1, leftString).flatMap((Tuple2<CompileState, Node> leftTuple) -> {
                    return ValueCompiler.parseNode(leftTuple.left(), rightString).flatMap((Tuple2<CompileState, Node> rightTuple) -> {
                        var left = leftTuple.right();
                        var right = rightTuple.right();
                        return new Some<Tuple2<CompileState, Node>>(new Tuple2Impl<CompileState, Node>(rightTuple.left(), new Operation(left, targetInfix, right)));
                    });
                });
            })).apply(input1);
        };
    }

    static Option<Tuple2<CompileState, Node>> parseSymbol(CompileState state, String input) {
        var stripped = Strings.strip(input);
        if (ValueCompiler.isSymbol(stripped)) {
            var withImport = TypeCompiler.addResolvedImportFromCache0(state, stripped);
            return new Some<Tuple2<CompileState, Node>>(new Tuple2Impl<CompileState, Node>(withImport, new Symbol(stripped)));
        }
        else {
            return new None<Tuple2<CompileState, Node>>();
        }
    }

    static boolean isSymbol(String input) {
        var query = new HeadedIter<Integer>(new RangeHead(Strings.length(input)));
        return query.allMatch((Integer index) -> {
            return ValueCompiler.isSymbolChar(index, input.charAt(index));
        });
    }

    private static boolean isSymbolChar(int index, char c) {
        return '_' == c
                || Characters.isLetter(c)
                || (0 != index && Characters.isDigit(c));
    }

    private static boolean isNumber(String input) {
        var query = new HeadedIter<Integer>(new RangeHead(Strings.length(input)));
        return query.map(input::charAt).allMatch((Character c) -> {
            return Characters.isDigit(c);
        });
    }

    private static Type resolve(CompileState state, Node value) {
        return switch (value) {
            case Invokable invokable -> invokable.resolve(state);
            case Lambda lambda -> lambda.resolve(state);
            case Not not -> not.resolve(state);
            case Operation operation -> operation.resolve(state);
            case Placeholder placeholder -> placeholder.resolve(state);
            case StringNode stringNode -> stringNode.resolve(state);
            case Symbol symbol -> symbol.resolve(state);
            default -> PrimitiveType.Unknown;
        };
    }

    static Option<Tuple2<CompileState, Node>> parseNumber(CompileState state, String input) {
        var stripped = Strings.strip(input);
        if (ValueCompiler.isNumber(stripped)) {
            return new Some<Tuple2<CompileState, Node>>(new Tuple2Impl<CompileState, Node>(state, new Symbol(stripped)));
        }
        else {
            return new None<Tuple2<CompileState, Node>>();
        }
    }

    static Tuple2<CompileState, String> compileNodeOrPlaceholder(CompileState state, String input) {
        return ValueCompiler.compileNode(state, input).orElseGet(() -> {
            return new Tuple2Impl<CompileState, String>(state, Placeholder.generatePlaceholder(input));
        });
    }

    static Option<Tuple2<CompileState, String>> compileNode(CompileState state, String input) {
        return ValueCompiler.parseNode(state, input).map((Tuple2<CompileState, Node> tuple) -> {
            return ValueCompiler.generateNode(tuple);
        });
    }

    private static Option<Tuple2<CompileState, Node>> parseArgument(CompileState state1, String input) {
        return ValueCompiler.parseNode(state1, input).map((Tuple2<CompileState, Node> tuple) -> {
            return new Tuple2Impl<CompileState, Node>(tuple.left(), tuple.right());
        });
    }

    private static Node transformCaller(CompileState state, Node oldNode) {
        return ValueCompiler.findChild(oldNode).flatMap((Node parent) -> {
            var parentType = ValueCompiler.resolve(state, parent);
            if (parentType.isFunctional()) {
                return new Some<Node>(parent);
            }

            return new None<Node>();
        }).orElse(oldNode);
    }

    private static Option<Node> findChild(Node oldNode) {
        if (oldNode.is("access")) {
            return new Some<Node>(oldNode.findNode("child").orElseGet(MapNode::new));
        }
        return new None<Node>();
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

    private static Option<Tuple2<CompileState, Node>> assembleInvokable(CompileState state, Node oldNode, String argsString) {
        return ValueCompiler.values((CompileState state1, String s) -> {
            return ValueCompiler.parseArgument(state1, s);
        }).apply(state, argsString).flatMap((Tuple2<CompileState, List<Node>> argsTuple) -> {
            var argsState = argsTuple.left();
            var args = argsTuple.right();

            var newCaller = ValueCompiler.transformCaller(argsState, oldNode);
            return new Some<Tuple2<CompileState, Node>>(new Tuple2Impl<CompileState, Node>(argsState, new Invokable(newCaller, args)));
        });
    }

    static <T, R> Iterable<R> retain(Iterable<T> args, Function<T, Option<R>> mapper) {
        return args.iter()
                .map(mapper)
                .flatMap(Iters::fromOption)
                .collect(new ListCollector<R>());
    }

    public static Option<Tuple2<CompileState, Node>> parseNode(CompileState state, String input) {
        return new OrRule<Node>(Lists.of(
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
                ValueCompiler::parseSymbol,
                ValueCompiler::parseNot,
                ValueCompiler::parseNumber,
                ValueCompiler.createOperatorRuleWithDifferentInfix("==", "==="),
                ValueCompiler.createOperatorRuleWithDifferentInfix("!=", "!=="),
                ValueCompiler.createTextRule("\""),
                ValueCompiler.createTextRule("'")
        )).apply(state, input);
    }

    public static <T> Rule<List<T>> values(Rule<T> mapper) {
        return new DivideRule<>(new ValueFolder(), mapper);
    }

    public static String getString(Node node) {
        if (node.is("construction")) {
            ConstructionCaller casted = (ConstructionCaller) (node);
            return "new " + casted.type();
        }

        return ValueCompiler.generateValue(node);
    }

    public static String generateValue(Node value) {
        if (value.is("access")) {
            Node child = value.findNode("child").orElseGet(MapNode::new);
            String property = value.findString("property").orElse("");
            return ValueCompiler.generateValue(child) + "." + property;
        }

        else if (value instanceof Invokable invokable) {
            return invokable.generate();
        }
        else if (value instanceof Lambda lambda) {
            return lambda.generate();
        }
        else if (value instanceof Not not) {
            return not.generate();
        }
        else if (value instanceof Operation operation) {
            return operation.generate();
        }
        else if (value instanceof Placeholder placeholder) {
            return placeholder.generate();
        }
        else if (value instanceof StringNode stringNode) {
            return stringNode.generate();
        }
        else if (value instanceof Symbol symbol) {
            return symbol.generate();
        }
        return "?";
    }
}