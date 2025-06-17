package magma.app.compile.list;

import magma.app.compile.FormattedError;
import magma.app.compile.node.Node;
import magma.app.compile.node.NodeErr;
import magma.app.compile.node.NodeResult;
import magma.app.compile.rule.NodeFactory;

import java.util.function.Supplier;

public record NodeListErr(FormattedError error) implements NodeListResult<Node, NodeResult<Node, FormattedError>> {
    @Override
    public NodeListResult<Node, NodeResult<Node, FormattedError>> add(Supplier<NodeResult<Node, FormattedError>> action) {
        return new NodeListErr(this.error);
    }

    @Override
    public NodeResult<Node, FormattedError> toNode(NodeFactory<Node> factory, String key) {
        return new NodeErr(this.error);
    }
}
