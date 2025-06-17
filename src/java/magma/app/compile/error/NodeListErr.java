package magma.app.compile.error;

import magma.app.compile.node.Node;

import java.util.function.Supplier;

public record NodeListErr(FormattedError error) implements NodeListResult {
    @Override
    public NodeListResult add(Supplier<NodeResult<Node>> action) {
        return new NodeListErr(this.error);
    }
}
