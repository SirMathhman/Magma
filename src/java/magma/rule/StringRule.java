package magma.rule;

import magma.node.MapNode;
import magma.node.Node;
import magma.node.result.NodeOk;
import magma.node.result.NodeResult;
import magma.option.Option;

public record StringRule(String key) implements Rule {
    @Override
    public NodeResult lex(final String input) {
        return new NodeOk(new MapNode().withString(this.key(), input));
    }

    @Override
    public Option<String> generate(final Node node) {
        return node.findString(this.key);
    }
}