package magma.app.compile.error.list;

import magma.api.Error;
import magma.app.compile.error.NodeListResult;
import magma.app.compile.error.NodeResult;
import magma.app.compile.error.NodeResults;
import magma.app.compile.node.NodeWithEverything;

import java.util.function.Supplier;

public record NodeListErr(Error error) implements NodeListResult<NodeWithEverything> {
    @Override
    public NodeResult<NodeWithEverything> toNode(String key) {
        return NodeResults.Err(this.error);
    }

    @Override
    public NodeListResult<NodeWithEverything> add(Supplier<NodeResult<NodeWithEverything>> other) {
        return this;
    }
}
