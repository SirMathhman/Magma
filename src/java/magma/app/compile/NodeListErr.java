package magma.app.compile;

import java.util.function.Supplier;

public record NodeListErr<Node, Error>(Error error) implements NodeListResult<Node, NodeResult<Node, Error>> {
    @Override
    public NodeListResult<Node, NodeResult<Node, Error>> add(Supplier<NodeResult<Node, Error>> action) {
        return new NodeListErr<>(this.error);
    }

    @Override
    public NodeResult<Node, Error> toNode(NodeFactory<Node> factory, String key) {
        return new NodeErr<>(this.error);
    }
}
