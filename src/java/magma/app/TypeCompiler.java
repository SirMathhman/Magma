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
import magma.app.compile.define.Placeholders;
import magma.app.compile.locate.FirstLocator;
import magma.app.compile.node.MapNode;
import magma.app.compile.node.Node;
import magma.app.compile.rule.OrRule;
import magma.app.compile.split.LocatingSplitter;
import magma.app.compile.split.Splitter;
import magma.app.io.Source;

public final class TypeCompiler {
    public static final Node Boolean = new MapNode("boolean").withString("value", "boolean");
    public static final Node Number = new MapNode("number").withString("value", "number");
    public static final Node String = new MapNode("string").withString("value", "string");
    public static final Node Unknown = new MapNode("unknown").withString("value", "unknown");
    public static final Node Var = new MapNode("var").withString("value", "var");
    public static final Node Void = new MapNode("void").withString("value", "void");
    public static final List<String> variants = Lists.of("boolean", "number", "string", "unknown", "var", "void");

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
            Node type = child.right();
            return new Some<Tuple2<CompileState, Node>>(new Tuple2Impl<CompileState, Node>(child.left(), new MapNode("variadic")
                    .withNode("child", type)));
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
            return new Some<Node>(TypeCompiler.String);
        }

        if (Strings.equalsTo("int", stripped) || Strings.equalsTo("Integer", stripped)) {
            return new Some<Node>(TypeCompiler.Number);
        }

        if (Strings.equalsTo("boolean", stripped) || Strings.equalsTo("Boolean", stripped)) {
            return new Some<Node>(TypeCompiler.Boolean);
        }

        if (Strings.equalsTo("var", stripped)) {
            return new Some<Node>(TypeCompiler.Var);
        }

        if (Strings.equalsTo("void", stripped)) {
            return new Some<Node>(TypeCompiler.Void);
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
                    return new Some<Tuple2<CompileState, Node>>(new Tuple2Impl<CompileState, Node>(compileState, new MapNode("template")
                            .withString("base", base)
                            .withNodeList("args", args)));
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
                        .withNode("returns", TypeCompiler.Void);
            });
        }

        if (Strings.equalsTo("Predicate", base)) {
            return args.findFirst().map((first) -> {
                List<Node> args1 = Lists.of(first);
                return new MapNode("functional")
                        .withNodeList("args", args1)
                        .withNode("returns", TypeCompiler.Boolean);
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
                    return new Tuple2Impl<CompileState, Node>(state, new MapNode("placeholder").withString("value", type));
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
        else if (type.is("placeholder")) {
            return ValueCompiler.generateValue(type);
        }
        else if (TypeCompiler.variants.contains(type.findString("value").orElse(""))) {
            return TypeCompiler.generateType(type);
        }
        else if (type.is("symbol")) {
            return ValueCompiler.generateValue(type);
        }
        else if (type.is("template")) {
            return type.findString("base").orElse("");
        }
        else if (type.is("variadic")) {
            return TypeCompiler.generateType(type);
        }

        return "?";
    }

    public static String generateBeforeName(Node type) {
        if (type.is("functional")) {
            return "";
        }
        else if (type.is("placeholder")) {
            return "";
        }
        else if (TypeCompiler.variants.contains(type.findString("value").orElse(""))) {
            return "";
        }
        else if (type.is("symbol")) {
            return "";
        }
        else if (type.is("template")) {
            return "";
        }
        else if (type.is("variadic")) {
            return "...";
        }
        throw new IllegalArgumentException();
    }

    public static String generateType(Node type) {
        if (type.is("functional")) {
            var joinedArguments = TypeCompiler.generateFunctionalArguments(type);
            Node returns = type.findNode("returns").orElse(new MapNode());
            return "(" + joinedArguments + ") => " + TypeCompiler.generateType(returns);
        }

        if (type.is("placeholder")) {
            var input = type.findString("value").orElse("");
            return Placeholders.generatePlaceholder(input);
        }

        if (TypeCompiler.variants.contains(type.findString("value").orElse(""))) {
            return type.findString("value").orElse("");
        }

        if (type.is("symbol")) {
            return type.findString("value").orElse("");
        }

        if (type.is("template")) {
            var base = type.findString("base").orElse("");
            String joined = TypeCompiler.joinTemplateArguments(type);
            return base + "<" + joined + ">";
        }

        if (type.is("variadic")) {
            Node child = type.findNode("child").orElse(new MapNode());
            return TypeCompiler.generateType(child) + "[]";
        }

        return "?";
    }

    private static java.lang.String joinTemplateArguments(Node type) {
        return type.findNodeList("args").orElse(Lists.empty()).iter()
                .map((Node arg) -> TypeCompiler.generateType(arg))
                .collect(new Joiner(", "))
                .orElse("");
    }

    private static String generateFunctionalArguments(Node type) {
        return type.findNodeList("args").orElse(Lists.empty())
                .iterWithIndices()
                .map((Tuple2<Integer, Node> tuple) -> "arg" + tuple.left() + " : " + TypeCompiler.generateType(tuple.right()))
                .collect(new Joiner(", "))
                .orElse("");
    }
}
