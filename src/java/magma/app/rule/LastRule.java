package magma.app.rule;

import magma.app.Node;
import magma.app.Rule;

import java.util.Optional;

public record LastRule(String infix, Rule childRule) implements Rule {
    @Override
    public Optional<Node> lex(String input) {
        final var separator = input.lastIndexOf(this.infix);
        if (separator < 0)
            return Optional.empty();

        final var child = input.substring(separator + this.infix.length());
        return this.childRule.lex(child);
    }
}