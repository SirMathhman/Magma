package magma;

import java.util.Optional;

public record StringRule(String key) {
    Optional<String> generate(final Node node) {
        return node.findString(this.key);
    }

    Optional<Node> lex(final String input) {
        return Optional.of(new MapNode().withString(this.key, input));
    }
}