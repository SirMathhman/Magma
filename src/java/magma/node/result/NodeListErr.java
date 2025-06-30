package magma.node.result;

import magma.node.EverythingNode;

public record NodeListErr() implements NodeListResult {
    @Override
    public NodeListResult add(final NodeResult<EverythingNode> other) {
        return new NodeListErr();
    }

    @Override
    public NodeResult<EverythingNode> toNode(final String key) {
        return new NodeErr<>();
    }
}
