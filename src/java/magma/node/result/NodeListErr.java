package magma.node.result;

import magma.error.FormatError;

public record NodeListErr<Node>(FormatError error) implements NodeListResult<Node> {
    @Override
    public NodeListResult<Node> add(final NodeResult<Node, FormatError> other) {
        return new NodeListErr<>(this.error);
    }

    @Override
    public NodeResult<Node, FormatError> toNode(final String key) {
        return new NodeErr<>(this.error);
    }
}
