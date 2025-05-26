package magma.app;

import jvm.api.collect.list.Lists;
import magma.api.Tuple2;
import magma.api.Tuple2Impl;
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
import magma.app.compile.type.FunctionType;
import magma.app.compile.type.Placeholder;
import magma.app.compile.type.PrimitiveNode;
import magma.app.compile.type.TemplateNode;
import magma.app.compile.type.VariadicType;
import magma.app.io.Source;

import java.util.Objects;

public final class TypeCompiler {
    public static Option<Tuple2<CompileState, String>> compileNode(CompileState state, String type) {
        return TypeCompiler.parseNode(state, type).map((Tuple2<CompileState, Node> tuple) -> {
            return new Tuple2Impl<CompileState, String>(tuple.left(), TypeCompiler.generateNode(tuple.right()));
        });
    }

    public static Option<Tuple2<CompileState, Node>> parseNode(CompileState state, String type) {
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
                    return TypeCompiler.compileNodeArgument(state1, s);
                }).apply(state, argsString).orElse(new Tuple2Impl<CompileState, List<String>>(state, Lists.empty()));
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

    private static Option<Tuple2<CompileState, Node>> assembleFunctionNode(CompileState state, String base, List<String> args) {
        return TypeCompiler.mapFunctionNode(base, args).map((Node generated) -> {
            return new Tuple2Impl<CompileState, Node>(state, generated);
        });
    }

    private static Option<Node> mapFunctionNode(String base, List<String> args) {
        if (Strings.equalsTo("Function", base)) {
            return args.findFirst().and(() -> {
                        return args.find(1);
                    })
                    .map((Tuple2<String, String> tuple) -> {
                        return new FunctionType(Lists.of(tuple.left()), tuple.right());
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
                    .map((Tuple2<Tuple2<String, String>, String> tuple) -> {
                        return new FunctionType(Lists.of(tuple.left().left(), tuple.left().right()), tuple.right());
                    });
        }

        if (Strings.equalsTo("Supplier", base)) {
            return args.findFirst().map((String first) -> {
                return new FunctionType(Lists.empty(), first);
            });
        }

        if (Strings.equalsTo("Consumer", base)) {
            return args.findFirst().map((String first) -> {
                return new FunctionType(Lists.of(first), "void");
            });
        }

        if (Strings.equalsTo("Predicate", base)) {
            return args.findFirst().map((String first) -> {
                return new FunctionType(Lists.of(first), "boolean");
            });
        }

        return new None<Node>();
    }

    private static Option<Tuple2<CompileState, String>> compileNodeArgument(CompileState state, String input) {
        return new OrRule<String>(Lists.of(
                (CompileState state2, String input1) -> {
                    return WhitespaceCompiler.compileWhitespace(state2, input1);
                },
                (CompileState state1, String type) -> {
                    return TypeCompiler.compileNode(state1, type);
                }
        )).apply(state, input);
    }

    private static Tuple2<CompileState, Node> parseNodeOrPlaceholder(CompileState state, String type) {
        return TypeCompiler.parseNode(state, type)
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
        if (Objects.requireNonNull(type) instanceof FunctionType functionNode) {
            return functionNode.generateSimple();
        }
        else if (type instanceof Placeholder placeholder) {
            return placeholder.generateSimple();
        }
        else if (type instanceof PrimitiveNode primitiveNode) {
            return primitiveNode.generateSimple();
        }
        else if (type.is("symbol")) {
            return ValueCompiler.generateValue(type);
        }
        else if (type instanceof TemplateNode templateNode) {
            return templateNode.generateSimple();
        }
        else if (type instanceof VariadicType variadicNode) {
            return variadicNode.generateSimple();
        }

        return "?";
    }

    public static String generateBeforeName(Node type) {
        if (Objects.requireNonNull(type) instanceof FunctionType functionNode) {
            return functionNode.generateBeforeName();
        }
        else if (type instanceof Placeholder placeholder) {
            return placeholder.generateBeforeName();
        }
        else if (type instanceof PrimitiveNode primitiveNode) {
            return primitiveNode.generateBeforeName();
        }
        else if (type.is("symbol")) {
            return "";
        }
        else if (type instanceof TemplateNode templateNode) {
            return templateNode.generateBeforeName();
        }
        else if (type instanceof VariadicType variadicNode) {
            return variadicNode.generateBeforeName();
        }
        throw new IllegalArgumentException();
    }

    public static String generateNode(Node type) {
        if (Objects.requireNonNull(type) instanceof FunctionType functionNode) {
            return functionNode.generateNode();
        }
        else if (type instanceof Placeholder placeholder) {
            return placeholder.generateNode();
        }
        else if (type instanceof PrimitiveNode primitiveNode) {
            return primitiveNode.generateNode();
        }
        else if (type.is("symbol")) {
            return type.findString("value").orElse("");
        }
        else if (type instanceof TemplateNode templateNode) {
            return templateNode.generateNode();
        }
        else if (type instanceof VariadicType variadicNode) {
            return variadicNode.generateNode();
        }
        return "?";
    }
}
