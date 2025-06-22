package magma.rule;

import magma.node.MapNode;
import magma.node.result.NodeOk;
import magma.node.result.NodeResult;

public record StringRule(String key) implements Rule {
    @Override
    public NodeResult lex(final String input) {
        return new NodeOk(new MapNode().withString(this.key(), input));
    }
}