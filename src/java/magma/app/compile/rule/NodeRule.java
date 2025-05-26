package magma.app.compile.rule;

import magma.api.option.Option;
import magma.app.compile.node.MapNode;
import magma.app.compile.node.Node;

public record NodeRule(String key, Rule<Node> rule) implements Rule<Node> {
    @Override
    public Option<Node> lex(String input) {
        return this.rule.lex(input).map(inner -> new MapNode().withNode(this.key, inner));
    }
}
