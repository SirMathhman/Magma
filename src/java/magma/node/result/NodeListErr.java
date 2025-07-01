package magma.node.result;

import magma.error.FormatError;

public record NodeListErr<Node>(FormatError error) implements NodeListResult<Node, FormatError> {
    @Override
    public NodeListResult<Node, FormatError> add(final NodeResult<Node, FormatError> other) {
        return new NodeListErr<>(this.error);
    }

    @Override
    public NodeResult<Node, FormatError> toNode(final String key) {
        return new NodeErr<>(this.error);
    }
}
