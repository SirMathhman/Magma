package magma.node.result;

import magma.string.result.StringResult;

public record NodeListErr<Node, error>(error error) implements NodeListResult<Node, error> {
    @Override
    public NodeListResult<Node, error> add(final NodeResult<Node, error, StringResult<error>> other) {
        return new NodeListErr<>(this.error);
    }

    @Override
    public NodeResult<Node, error, StringResult<error>> toNode(final String key) {
        return new NodeErr<>(this.error);
    }
}
