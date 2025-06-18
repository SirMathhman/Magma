package magma.app;

import java.util.Optional;

public record StringRule(String key) {
    public Optional<String> generate(Node node) {
        return node.findString(this.key);
    }

    public Optional<Node> lex(String input) {
        return Optional.of(new MapNode().withString(this.key, input));
    }
}