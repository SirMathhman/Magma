package magma.app;

import java.util.Optional;

public record LastRule(String infix, Rule child) implements Rule {
    @Override
    public Optional<Node> lex(final String input) {
        final var separator = input.lastIndexOf(this.infix);
        if (0 > separator)
            return Optional.empty();

        final var infixLength = this.infix.length();
        final var destination = input.substring(separator + infixLength);
        return this.child.lex(destination);
    }
}