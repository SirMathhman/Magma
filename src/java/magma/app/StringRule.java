package magma.app;

import java.util.Optional;

public record StringRule(String key) implements Rule {
    @Override
    public Optional<Node> lex(final String input) {
        final var node = MapNode.empty()
                .withString(this.key, input);

        return Optional.of(node);
    }
}