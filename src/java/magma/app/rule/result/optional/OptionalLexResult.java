package magma.app.rule.result.optional;

import magma.app.rule.result.LexResult;

import java.util.Optional;
import java.util.function.Function;

public record OptionalLexResult<N>(Optional<N> maybeValue) implements LexResult<N> {
    public static <N> LexResult<N> createEmpty() {
        return new OptionalLexResult<>(Optional.empty());
    }

    public static <N> LexResult<N> of(N value) {
        return new OptionalLexResult<>(Optional.of(value));
    }

    @Override
    public LexResult<N> flatMap(Function<N, LexResult<N>> mapper) {
        return new OptionalLexResult<N>(this.maybeValue.flatMap(mapNode -> mapper.apply(mapNode).findValue()));
    }

    @Override
    public LexResult<N> mapValue(Function<N, N> mapper) {
        return new OptionalLexResult<N>(this.maybeValue.map(mapper));
    }

    @Override
    public Optional<N> findValue() {
        return this.maybeValue;
    }
}
