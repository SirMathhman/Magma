package magma.node.result;

import magma.error.CompileError;
import magma.node.EverythingNode;

public record NodeListErr(CompileError error) implements NodeListResult {
    @Override
    public NodeListResult add(final NodeResult<EverythingNode> other) {
        return new NodeListErr(this.error);
    }

    @Override
    public NodeResult<EverythingNode> toNode(final String key) {
        return new NodeErr<>(this.error);
    }
}
