package magma.app.compile.error.node;

import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Supplier;

public record NodeOk<Node>(Node node) implements NodeResult<Node> {
    @Override
    public Optional<Node> findValue() {
        return Optional.of(this.node);
    }

    @Override
    public NodeResult<Node> mergeResult(Supplier<NodeResult<Node>> other, BiFunction<Node, Node, Node> merger) {
        return other.get()
                .mergeNode(this.node, merger);
    }

    @Override
    public NodeResult<Node> mergeNode(Node node, BiFunction<Node, Node, Node> merger) {
        return new NodeOk<>(merger.apply(this.node, node));
    }
}
