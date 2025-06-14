package magma.app.rule.result;

import magma.app.node.Node;

import java.util.Optional;
import java.util.function.Function;

public record LexResult(Optional<Node> value) {
    public Node orElse(Node other) {
        return this.value.orElse(other);
    }

    public LexResult flatMap(Function<Node, LexResult> mapper) {
        return new LexResult(this.value.flatMap(mapNode -> mapper.apply(mapNode).value));
    }
}
