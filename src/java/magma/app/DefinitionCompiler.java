package magma.app;

import jvm.api.collect.list.Lists;
import magma.api.Tuple2;
import magma.api.Tuple2Impl;
import magma.api.collect.Iters;
import magma.api.collect.Joiner;
import magma.api.collect.list.Iterable;
import magma.api.collect.list.List;
import magma.api.collect.list.ListCollector;
import magma.api.option.None;
import magma.api.option.Some;
import magma.app.compile.CompileState;
import magma.app.compile.define.Definition;
import magma.app.compile.node.Node;

final class DefinitionCompiler {
    public static Iterable<Definition> retainDefinitionsFromParameters(Iterable<Node> parameters) {
        return parameters.iter()
                .map((Node node) -> {
                    return node instanceof Definition definition ? new Some<>(definition) : new None<Definition>();
                })
                .flatMap(Iters::fromOption)
                .collect(new ListCollector<Definition>());
    }

    public static String joinParameters(Iterable<Definition> parameters) {
        return parameters.iter()
                .map((Definition definition) -> {
                    return DefiningCompiler.generateParameter(definition);
                })
                .map((String generated) -> {
                    return "\n\t" + generated + ";";
                })
                .collect(Joiner.empty())
                .orElse("");
    }

    public static Tuple2<CompileState, List<Node>> parseParameters(CompileState state, String params) {
        return ValueCompiler.values((CompileState state1, String s) -> {
            return new Some<Tuple2<CompileState, Node>>(DefiningCompiler.parseParameterOrPlaceholder(state1, s));
        }).apply(state, params).orElse(new Tuple2Impl<CompileState, List<Node>>(state, Lists.empty()));
    }
}