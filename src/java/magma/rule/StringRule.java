package magma.rule;

import magma.node.MapNode;
import magma.node.Node;
import magma.node.result.NodeOk;
import magma.node.result.NodeResult;
import magma.string.StringErr;
import magma.string.StringResult;

public record StringRule(String key) implements Rule<Node, StringResult> {
    @Override
    public NodeResult lex(final String input) {
        return new NodeOk(new MapNode().withString(this.key(), input));
    }

    @Override
    public StringResult generate(final Node node) {
        return node.findString(this.key)
                .<StringResult>map(StringOk::new)
                .orElseGet(StringErr::new);
    }
}