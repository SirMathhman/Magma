package magma.app.compile.error;

import magma.app.compile.node.NodeWithEverything;

import java.util.function.Supplier;

public record NodeListErr<Error>(
        Error error) implements NodeListResult<NodeWithEverything, Error, NodeResult<NodeWithEverything, Error>> {
    @Override
    public NodeResult<NodeWithEverything, Error> toNode(String key) {
        return new NodeErr<>(this.error);
    }

    @Override
    public NodeListResult<NodeWithEverything, Error, NodeResult<NodeWithEverything, Error>> add(Supplier<NodeResult<NodeWithEverything, Error>> other) {
        return this;
    }
}
