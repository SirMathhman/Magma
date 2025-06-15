package magma.app.rule;

import magma.app.Node;
import magma.app.Rule;

import java.util.Optional;

public record StripRule(Rule rule) implements Rule {
    @Override
    public Optional<Node> lex(String input) {
        final var stripped = input.strip();
        return this.rule.lex(stripped);
    }
}