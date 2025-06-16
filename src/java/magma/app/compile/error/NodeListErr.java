package magma.app.compile.error;

import java.util.function.Supplier;

public record NodeListErr<Node, Error>(Error error) implements NodeListResult<Node, Error, NodeResult<Node, Error>> {
    @Override
    public NodeResult<Node, Error> toNode(String key) {
        return new NodeErr<>(this.error);
    }

    @Override
    public NodeListResult<Node, Error, NodeResult<Node, Error>> add(Supplier<NodeResult<Node, Error>> other) {
        return this;
    }
}
