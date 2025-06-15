package magma.app;

import java.util.Optional;

public record StripRule(PrefixRule rule) {
    public Optional<Node> lex(String input) {
        final var stripped = input.strip();
        return this.rule.lex(stripped);
    }
}