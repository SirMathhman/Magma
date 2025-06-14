package magma.app.rule.result.optional;

import magma.app.node.core.MergingNode;
import magma.app.rule.result.LexResult;
import magma.app.rule.result.MergingLexResult;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

public record OptionalLexResult<N extends MergingNode<N>>(Optional<N> maybeValue) implements MergingLexResult<N> {
    public static <N extends MergingNode<N>> MergingLexResult<N> createEmpty() {
        return new OptionalLexResult<N>(Optional.empty());
    }

    public static <N extends MergingNode<N>> MergingLexResult<N> of(N value) {
        return new OptionalLexResult<>(Optional.of(value));
    }

    @Override
    public MergingLexResult<N> flatMap(Function<N, MergingLexResult<N>> mapper) {
        return new OptionalLexResult<N>(this.maybeValue.flatMap(mapNode -> mapper.apply(mapNode).findValue()));
    }

    @Override
    public MergingLexResult<N> merge(Supplier<LexResult<N, MergingLexResult<N>>> other) {
        return this.flatMap(value -> other.get().mapValue(value::merge));
    }

    @Override
    public MergingLexResult<N> mapValue(Function<N, N> mapper) {
        return new OptionalLexResult<N>(this.maybeValue.map(mapper));
    }

    @Override
    public Optional<N> findValue() {
        return this.maybeValue;
    }
}
