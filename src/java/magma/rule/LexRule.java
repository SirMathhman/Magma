package magma.rule;

import magma.node.MapNode;
import magma.node.Node;
import magma.option.Option;
import magma.option.Some;

public record LexRule(String key) {
    public Option<Node> lex(final String input) {
        return new Some<>(new MapNode().withString(this.key(), input));
    }
}