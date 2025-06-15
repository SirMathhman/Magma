package magma.app.rule;

import magma.app.Node;
import magma.app.Rule;
import magma.app.maybe.NodeResult;
import magma.app.maybe.StringResult;
import magma.app.maybe.node.OkNodeResult;
import magma.app.node.MapNode;

public record StringRule(String key) implements Rule<Node, NodeResult<Node>, StringResult> {
    @Override
    public StringResult generate(Node node) {
        return node.findString(this.key);
    }

    @Override
    public NodeResult<Node> lex(String input) {
        return new OkNodeResult<Node>(new MapNode().withString(this.key, input));
    }
}