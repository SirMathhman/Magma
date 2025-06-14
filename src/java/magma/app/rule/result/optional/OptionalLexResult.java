package magma.app.rule.result.optional;

import magma.app.node.CompoundNode;
import magma.app.rule.result.LexResult;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

public record OptionalLexResult(Optional<CompoundNode> maybeValue) implements LexResult {
    public static LexResult createEmpty() {
        return new OptionalLexResult(Optional.empty());
    }

    public static LexResult of(CompoundNode value) {
        return new OptionalLexResult(Optional.of(value));
    }

    @Override
    public LexResult flatMap(Function<CompoundNode, LexResult> mapper) {
        return new OptionalLexResult(this.maybeValue.flatMap(mapNode -> mapper.apply(mapNode).findValue()));
    }

    @Override
    public LexResult merge(Supplier<LexResult> other) {
        return this.flatMap(value -> other.get().mapValue(value::merge));
    }

    @Override
    public LexResult mapValue(Function<CompoundNode, CompoundNode> mapper) {
        return new OptionalLexResult(this.maybeValue.map(mapper));
    }

    @Override
    public Optional<CompoundNode> findValue() {
        return this.maybeValue;
    }
}
