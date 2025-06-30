package magma.rule;

import magma.node.MapNode;
import magma.node.Node;

import java.util.Optional;

public record StringRule(String key) implements Rule<Node> {
    @Override
    public Optional<Node> lex(final String input) {
        final var node = new MapNode().withString(this.key, input);
        return Optional.of(node);
    }

    @Override
    public Optional<String> generate(final Node node) {
        return node.findString(this.key);
    }
}