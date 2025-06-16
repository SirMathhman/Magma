package magma.app.compile.error;

import magma.app.compile.node.NodeWithEverything;

import java.util.function.Supplier;

public record NodeListErr(FormattedError error) implements NodeListResult<NodeWithEverything> {
    @Override
    public NodeResult<NodeWithEverything> toNode(String key) {
        return NodeResults.Err(this.error);
    }

    @Override
    public NodeListResult<NodeWithEverything> add(Supplier<NodeResult<NodeWithEverything>> other) {
        return this;
    }
}
