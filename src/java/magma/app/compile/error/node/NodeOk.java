package magma.app.compile.error.node;

import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

public record NodeOk<Node>(Node value) implements NodeResult<Node> {
    @Override
    public Optional<Node> findValue() {
        return Optional.of(this.value);
    }

    @Override
    public NodeResult<Node> mergeResult(Supplier<NodeResult<Node>> other, BiFunction<Node, Node, Node> merger) {
        return other.get()
                .mergeNode(this.value, merger);
    }

    @Override
    public NodeResult<Node> mergeNode(Node node, BiFunction<Node, Node, Node> merger) {
        return new NodeOk<>(merger.apply(this.value, node));
    }

    @Override
    public NodeResult<Node> map(Function<Node, Node> mapper) {
        return new NodeOk<>(mapper.apply(this.value));
    }

}
