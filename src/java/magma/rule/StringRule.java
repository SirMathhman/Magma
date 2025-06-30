package magma.rule;

import magma.node.EverythingNode;
import magma.node.MapNode;

import java.util.Optional;

public record StringRule(String key) implements Rule<EverythingNode> {
    @Override
    public Optional<EverythingNode> lex(final String input) {
        final var node = new MapNode().withString(this.key, input);
        return Optional.of(node);
    }

    @Override
    public Optional<String> generate(final EverythingNode node) {
        return node.findString(this.key);
    }
}