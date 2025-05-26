package magma.app;

import jvm.api.collect.list.Lists;
import magma.api.Tuple2;
import magma.api.Tuple2Impl;
import magma.api.collect.Joiner;
import magma.api.collect.list.List;
import magma.api.option.None;
import magma.api.option.Option;
import magma.api.option.Some;
import magma.api.text.Strings;
import magma.app.compile.CompileState;
import magma.app.compile.Dependency;
import magma.app.compile.Import;
import magma.app.compile.Registry;
import magma.app.compile.compose.Composable;
import magma.app.compile.compose.SplitComposable;
import magma.app.compile.compose.SuffixComposable;
import magma.app.compile.locate.FirstLocator;
import magma.app.compile.node.MapNode;
import magma.app.compile.node.Node;
import magma.app.compile.rule.OrRule;
import magma.app.compile.split.LocatingSplitter;
import magma.app.compile.split.Splitter;
import magma.app.compile.define.Placeholder;
import magma.app.compile.type.PrimitiveNode;
import magma.app.compile.type.TemplateNode;
import magma.app.compile.type.VariadicType;
import magma.app.io.Source;

public final class TypeCompiler {
    public static Option<Tuple2<CompileState, String>> compileType(CompileState state, String type) {
        return TypeCompiler.parseType(state, type).map((Tuple2<CompileState, Node> tuple) -> {
            return new Tuple2Impl<CompileState, String>(tuple.left(), TypeCompiler.generateType(tuple.right()));
        });
    }

    public static Option<Tuple2<CompileState, Node>> parseType(CompileState state, String type) {
        return new OrRule<Node>(Lists.of(
                TypeCompiler::parseVarArgs,
                TypeCompiler::parseGeneric,
                TypeCompiler::parsePrimitive,
                TypeCompiler::parseSymbolNode
        )).apply(state, type);
    }

    private static Option<Tuple2<CompileState, Node>> parseVarArgs(CompileState state, String input) {
        var stripped = Strings.strip(input);
        return new SuffixComposable<Tuple2<CompileState, Node>>("...", (String s) -> {
            var child = TypeCompiler.parseNodeOrPlaceholder(state, s);
            return new Some<Tuple2<CompileState, Node>>(new Tuple2Impl<CompileState, Node>(child.left(), new VariadicType(child.right())));
        }).apply(stripped);
    }

    private static Option<Tuple2<CompileState, Node>> parseSymbolNode(CompileState state, String input) {
        var stripped = Strings.strip(input);
        if (ValueCompiler.isSymbol(stripped)) {
            CompileState resolved = TypeCompiler.addResolvedImportFromCache0(state, stripped);
            return new Some<Tuple2<CompileState, Node>>(new Tuple2Impl<CompileState, Node>(resolved, new MapNode("symbol").withString("value", stripped)));
        }
        return new None<Tuple2<CompileState, Node>>();
    }

    private static Option<Tuple2<CompileState, Node>> parsePrimitive(CompileState state, String input) {
        return TypeCompiler.findPrimitiveNode(Strings.strip(input)).map((Node result) -> {
            return new Tuple2Impl<CompileState, Node>(state, result);
        });
    }

    private static Option<Node> findPrimitiveNode(String input) {
        var stripped = Strings.strip(input);
        if (Strings.equalsTo("char", stripped) || Strings.equalsTo("Character", stripped) || Strings.equalsTo("String", stripped)) {
            return new Some<Node>(PrimitiveNode.String);
        }

        if (Strings.equalsTo("int", stripped) || Strings.equalsTo("Integer", stripped)) {
            return new Some<Node>(PrimitiveNode.Number);
        }

        if (Strings.equalsTo("boolean", stripped) || Strings.equalsTo("Boolean", stripped)) {
            return new Some<Node>(PrimitiveNode.Boolean);
        }

        if (Strings.equalsTo("var", stripped)) {
            return new Some<Node>(PrimitiveNode.Var);
        }

        if (Strings.equalsTo("void", stripped)) {
            return new Some<Node>(PrimitiveNode.Void);
        }

        return new None<Node>();
    }

    private static Option<Tuple2<CompileState, Node>> parseGeneric(CompileState state, String input) {
        return new SuffixComposable<Tuple2<CompileState, Node>>(">", (String withoutEnd) -> {
            Splitter splitter = new LocatingSplitter("<", new FirstLocator());
            return new SplitComposable<Tuple2<CompileState, Node>>(splitter, Composable.toComposable((String baseString, String argsString) -> {
                var argsTuple = ValueCompiler.values((CompileState state1, String s) -> {
                    return new OrRule<Node>(Lists.of(
                            (CompileState state2, String input1) -> {
                                return WhitespaceCompiler.parseWhitespace(state2, input1).map(type -> new Tuple2Impl<>(type.left(), type.right()));
                            },
                            (CompileState state2, String type) -> {
                                return TypeCompiler.parseType(state2, type);
                            }
                    )).apply(state1, s);
                }).apply(state, argsString).orElse(new Tuple2Impl<>(state, Lists.empty()));
                var argsState = argsTuple.left();
                var args = argsTuple.right();

                var base = Strings.strip(baseString);
                return TypeCompiler.assembleFunctionNode(argsState, base, args).or(() -> {
                    var compileState = TypeCompiler.addResolvedImportFromCache0(argsState, base);
                    return new Some<Tuple2<CompileState, Node>>(new Tuple2Impl<CompileState, Node>(compileState, new TemplateNode(base, args)));
                });
            })).apply(withoutEnd);
        }).apply(Strings.strip(input));
    }

    private static Option<Tuple2<CompileState, Node>> assembleFunctionNode(CompileState state, String base, List<Node> args) {
        return TypeCompiler.mapFunctionNode(base, args).map((Node generated) -> {
            return new Tuple2Impl<CompileState, Node>(state, generated);
        });
    }

    private static Option<Node> mapFunctionNode(String base, List<Node> args) {
        if (Strings.equalsTo("Function", base)) {
            return args.findFirst().and(() -> {
                        return args.find(1);
                    })
                    .map((Tuple2<Node, Node> tuple) -> {
                        List<Node> args1 = Lists.of(tuple.left());
                        Node returns = tuple.right();
                        return new MapNode("functional")
                                .withNodeList("args", args1)
                                .withNode("returns", returns);
                    });
        }

        if (Strings.equalsTo("BiFunction", base)) {
            return args.find(0)
                    .and(() -> {
                        return args.find(1);
                    })
                    .and(() -> {
                        return args.find(2);
                    })
                    .map(tuple -> {
                        List<Node> args1 = Lists.of(tuple.left().left(), tuple.left().right());
                        Node returns = tuple.right();
                        return new MapNode("functional")
                                .withNodeList("args", args1)
                                .withNode("returns", returns);
                    });
        }

        if (Strings.equalsTo("Supplier", base)) {
            return args.findFirst().map((first) -> {
                List<Node> args1 = Lists.empty();
                return new MapNode("functional")
                        .withNodeList("args", args1)
                        .withNode("returns", first);
            });
        }

        if (Strings.equalsTo("Consumer", base)) {
            return args.findFirst().map((first) -> {
                List<Node> args1 = Lists.of(first);
                return new MapNode("functional")
                        .withNodeList("args", args1)
                        .withNode("returns", PrimitiveNode.Void);
            });
        }

        if (Strings.equalsTo("Predicate", base)) {
            return args.findFirst().map((first) -> {
                List<Node> args1 = Lists.of(first);
                return new MapNode("functional")
                        .withNodeList("args", args1)
                        .withNode("returns", PrimitiveNode.Boolean);
            });
        }

        return new None<Node>();
    }

    private static Tuple2<CompileState, Node> parseNodeOrPlaceholder(CompileState state, String type) {
        return TypeCompiler.parseType(state, type)
                .map((Tuple2<CompileState, Node> tuple) -> {
                    return new Tuple2Impl<CompileState, Node>(tuple.left(), tuple.right());
                })
                .orElseGet(() -> {
                    return new Tuple2Impl<CompileState, Node>(state, new Placeholder(type));
                });
    }

    private static CompileState getState(CompileState immutableCompileState, Location location) {
        var requestedNamespace = location.namespace();
        var requestedChild = location.name();

        var namespace = TypeCompiler.fixNamespace(requestedNamespace, immutableCompileState.context().findNamespaceOrEmpty());
        if (immutableCompileState.registry().doesImportExistAlready(requestedChild)) {
            return immutableCompileState;
        }

        var namespaceWithChild = namespace.addLast(requestedChild);
        var anImport = new Import(namespaceWithChild, requestedChild);
        return immutableCompileState.mapRegistry((Registry registry) -> {
            return registry.addImport(anImport);
        });
    }

    public static CompileState addResolvedImportFromCache0(CompileState state, String base) {
        if (state.stack().hasAnyStructureName(base)) {
            return state;
        }

        return state.context()
                .findSource(base)
                .map((Source source) -> {
                    Location location = source.createLocation();
                    return TypeCompiler.getCompileState1(state, location)
                            .orElseGet(() -> {
                                return TypeCompiler.getState(state, location);
                            });
                })
                .orElse(state);
    }

    private static Option<CompileState> getCompileState1(CompileState immutableCompileState, Location location) {
        if (!immutableCompileState.context().hasPlatform(Platform.PlantUML)) {
            return new None<CompileState>();
        }

        var name = immutableCompileState.context().findNameOrEmpty();
        var dependency = new Dependency(name, location.name());
        if (immutableCompileState.registry().containsDependency(dependency)) {
            return new None<CompileState>();
        }

        return new Some<CompileState>(immutableCompileState.mapRegistry((Registry registry1) -> {
            return registry1.addDependency(dependency);
        }));
    }

    private static List<String> fixNamespace(List<String> requestedNamespace, List<String> thisNamespace) {
        if (thisNamespace.isEmpty()) {
            return requestedNamespace.addFirst(".");
        }

        return TypeCompiler.addParentSeparator(requestedNamespace, thisNamespace.size());
    }

    private static List<String> addParentSeparator(List<String> newNamespace, int count) {
        var index = 0;
        var copy = newNamespace;
        while (index < count) {
            copy = copy.addFirst("..");
            index++;
        }

        return copy;
    }

    public static String generateSimple(Node type) {
        if (type.is("functional")) {
            return TypeCompiler.generateType(type);
        }
        else if (type instanceof Placeholder placeholder) {
            return ValueCompiler.generateValue(placeholder);
        }
        else if (type instanceof PrimitiveNode primitiveNode) {
            return primitiveNode.generateSimple();
        }
        else if (type.is("symbol")) {
            return ValueCompiler.generateValue(type);
        }
        else if (type instanceof TemplateNode templateNode) {
            return templateNode.base();
        }
        else if (type instanceof VariadicType variadicNode) {
            return variadicNode.generateSimple();
        }

        return "?";
    }

    public static String generateBeforeName(Node type) {
        if (type.is("functional")) {
            return "";
        }
        else if (type instanceof Placeholder placeholder) {
            return "";
        }
        else if (type instanceof PrimitiveNode primitiveNode) {
            return primitiveNode.generateBeforeName();
        }
        else if (type.is("symbol")) {
            return "";
        }
        else if (type instanceof TemplateNode templateNode) {
            return "";
        }
        else if (type instanceof VariadicType variadicNode) {
            return variadicNode.generateBeforeName();
        }
        throw new IllegalArgumentException();
    }

    public static String generateType(Node type) {
        if (type.is("functional")) {
            var joinedArguments = TypeCompiler.generateFunctionalArguments(type);
            return "(" + joinedArguments + ") => " + TypeCompiler.generateType(type.findNode("returns").orElse(new MapNode()));
        }
        else if (type instanceof Placeholder placeholder) {
            return Placeholder.generatePlaceholder(placeholder.input());
        }
        else if (type instanceof PrimitiveNode primitiveNode) {
            return primitiveNode.generateNode();
        }
        else if (type.is("symbol")) {
            return type.findString("value").orElse("");
        }
        else if (type instanceof TemplateNode(String base, List<Node> args)) {
            String joined = args.iter()
                    .map((Node arg) -> TypeCompiler.generateType(arg))
                    .collect(new Joiner(", "))
                    .orElse("");

            return base + "<" + joined + ">";
        }
        else if (type instanceof VariadicType variadicNode) {
            return variadicNode.generateNode();
        }
        return "?";
    }

    private static String generateFunctionalArguments(Node type) {
        return type.findNodeList("args").orElse(Lists.empty())
                .iterWithIndices()
                .map((Tuple2<Integer, Node> tuple) -> "arg" + tuple.left() + " : " + TypeCompiler.generateType(tuple.right()))
                .collect(new Joiner(", "))
                .orElse("");
    }
}
