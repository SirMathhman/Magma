package magma.app.compile.error;

import magma.app.compile.node.Node;

import java.util.function.Supplier;

public record NodeErr(FormattedError node) implements NodeResult {
    @Override
    public NodeResult mergeResult(Supplier<NodeResult> other) {
        return new NodeErr(this.node);
    }

    @Override
    public NodeResult mergeNode(Node value1) {
        return new NodeErr(this.node);
    }

    @Override
    public NodeResult retype(String type) {
        return new NodeErr(this.node);
    }
}
