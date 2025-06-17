package magma.app.compile.error.list;

import magma.app.compile.error.FormattedError;
import magma.app.compile.error.node.NodeResult;
import magma.app.compile.node.Node;

import java.util.function.Supplier;

public record NodeListErr(FormattedError error) implements NodeListResult {
    @Override
    public NodeListResult add(Supplier<NodeResult<Node>> action) {
        return new NodeListErr(this.error);
    }
}
