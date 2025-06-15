package magma.app;

import java.util.Optional;

public record StringRule(String key) {
    public Optional<Node> lex(String input) {
        return Optional.of(new Node().withString(this.key, input));
    }
}