package magma.app.compile.error;

import magma.app.compile.node.Node;

import java.util.function.Supplier;

public record NodeOk(Node node) implements NodeResult {
    @Override
    public NodeResult mergeResult(Supplier<NodeResult> other) {
        return other.get()
                .mergeNode(this.node);
    }

    @Override
    public NodeResult mergeNode(Node value1) {
        return new NodeOk(this.node.merge(value1));
    }
}
