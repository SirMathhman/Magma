package magma.rule;

import magma.node.MapNode;
import magma.node.Node;
import magma.node.result.NodeOk;
import magma.node.result.NodeResult;
import magma.string.StringResult;

public class EmptyRule implements Rule<Node, StringResult> {
    @Override
    public NodeResult lex(final String input) {
        return new NodeOk(new MapNode());
    }

    @Override
    public StringResult generate(final Node node) {
        return new StringOk("");
    }
}
