package magma.app.rule.result;

import magma.app.node.Node;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

public record LexResult(Optional<Node> maybeValue) {
    public static LexResult createEmpty() {
        return LexResult.createEmpty();
    }

    public LexResult flatMap(Function<Node, LexResult> mapper) {
        return new LexResult(this.maybeValue.flatMap(mapNode -> mapper.apply(mapNode).maybeValue));
    }

    public LexResult merge(Supplier<LexResult> other) {
        return this.flatMap(value -> other.get().mapValue(value::merge));
    }

    private LexResult mapValue(Function<Node, Node> mapper) {
        return new LexResult(this.maybeValue.map(mapper));
    }
}
