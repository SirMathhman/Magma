package magma.node.result;

public record NodeListErr<Node, Error, StringResult>(Error error)
        implements NodeListResult<NodeResult<Node, Error, StringResult>> {
    @Override
    public NodeListResult<NodeResult<Node, Error, StringResult>> add(final NodeResult<Node, Error, StringResult> other) {
        return new NodeListErr<>(this.error);
    }

    @Override
    public NodeResult<Node, Error, StringResult> toNode(final String key) {
        return new NodeErr<>(this.error);
    }
}
