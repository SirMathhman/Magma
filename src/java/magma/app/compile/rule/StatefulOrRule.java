package magma.app.compile.rule;

import magma.api.Tuple2;
import magma.api.Tuple2Impl;
import magma.api.collect.Iters;
import magma.api.collect.list.Iterable;
import magma.api.option.Option;
import magma.app.compile.CompileState;
import magma.app.compile.define.Placeholders;

public record StatefulOrRule<T>(Iterable<StatefulRule<T>> rules) implements StatefulRule<T> {
    public static Tuple2<CompileState, String> compileOrPlaceholder(
            CompileState state,
            String input,
            Iterable<StatefulRule<String>> rules
    ) {
        return new StatefulOrRule<String>(rules).apply(state, input).orElseGet(() -> {
            return new Tuple2Impl<CompileState, String>(state, Placeholders.generatePlaceholder(input));
        });
    }

    @Override
    public Option<Tuple2<CompileState, T>> apply(CompileState state, String input) {
        return this.rules.iter()
                .map((StatefulRule<T> statefulRule) -> statefulRule.apply(state, input))
                .flatMap(Iters::fromOption)
                .next();
    }
}