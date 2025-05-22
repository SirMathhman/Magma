package magma.app.compile.rule;

import magma.api.Tuple2;
import magma.api.option.Option;
import magma.app.compile.CompileState;
import magma.app.compile.compose.Composable;
import magma.app.compile.value.Value;

import java.util.function.Function;

public record ComposableRule(
        Function<CompileState, Composable<String, Tuple2<CompileState, Value>>> mapper) implements Rule<Value> {
    @Override
    public Option<Tuple2<CompileState, Value>> apply(CompileState state, String input) {
        return this.mapper.apply(state).apply(input);
    }

}
