package magma.app.compile.rule;

import magma.api.option.Option;
import magma.app.compile.node.MapNode;
import magma.app.compile.node.Node;

public record NodeRule(String key, Rule childRule) implements Rule {
    @Override
    public Option<Node> lex(String input) {
        return this.childRule.lex(input).map(inner -> new MapNode().withNode(this.key, inner));
    }
}
