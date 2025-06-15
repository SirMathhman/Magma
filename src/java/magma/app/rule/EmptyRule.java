package magma.app.rule;

import magma.app.Node;
import magma.app.Rule;
import magma.app.maybe.NodeResult;
import magma.app.maybe.StringResult;
import magma.app.maybe.node.OkNodeResult;
import magma.app.maybe.string.OkStringResult;
import magma.app.node.MapNode;

public class EmptyRule implements Rule<Node, NodeResult<Node>, StringResult> {
    @Override
    public StringResult generate(Node node) {
        return new OkStringResult("");
    }

    @Override
    public NodeResult<Node> lex(String input) {
        return new OkNodeResult<>(new MapNode());
    }
}
