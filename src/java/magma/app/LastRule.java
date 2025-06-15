package magma.app;

import java.util.Optional;

public record LastRule(String infix, StringRule childRule) {
    Optional<Node> lex(String input) {
        final var separator = input.lastIndexOf(this.infix);
        if (separator < 0)
            return Optional.empty();

        final var child = input.substring(separator + this.infix.length());
        return this.childRule.lex(child);
    }
}