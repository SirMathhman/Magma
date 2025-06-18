package magma.app;

import java.util.Optional;

public record LastRule(String infix, StringRule rule) {
    public Optional<Node> lex(String input) {
        final var index = input.lastIndexOf(this.infix());
        if (index < 0)
            return Optional.empty();

        final var slice = input.substring(index + this.infix()
                .length());
        return this.rule()
                .lex(slice);
    }
}