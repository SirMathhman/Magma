package magma.app.compile;

import java.util.function.Supplier;

public record NodeListErr(FormattedError error) implements NodeListResult<Node, NodeResult<Node, FormattedError>> {
    @Override
    public NodeListResult<Node, NodeResult<Node, FormattedError>> add(Supplier<NodeResult<Node, FormattedError>> action) {
        return new NodeListErr(this.error);
    }

    @Override
    public NodeResult<Node, FormattedError> toNode(NodeFactory<Node> factory, String key) {
        return new NodeErr(this.error);
    }
}
