package magma.rule;

import magma.node.MapNode;
import magma.node.Node;
import magma.option.Option;
import magma.option.Some;

public record StringRule(String key) implements Rule {
    @Override
    public Option<Node> lex(final String input) {
        return new Some<>(new MapNode().withString(this.key(), input));
    }
}