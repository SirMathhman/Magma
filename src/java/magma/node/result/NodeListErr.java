package magma.node.result;

import magma.error.FormatError;

public record NodeListErr<Node>(FormatError error) implements NodeListResult<Node> {
    @Override
    public NodeListResult<Node> add(final NodeResult<Node> other) {
        return new NodeListErr<>(this.error);
    }

    @Override
    public NodeResult<Node> toNode(final String key) {
        return new NodeErr<>(this.error);
    }
}
