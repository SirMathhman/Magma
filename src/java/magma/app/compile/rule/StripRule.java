package magma.app.compile.rule;

import magma.app.compile.Node;
import magma.app.compile.Rule;

import java.util.Optional;

public record StripRule(Rule rule) implements Rule {
    @Override
    public Optional<Node> lex(String input) {
        final var stripped = input.strip();
        return this.rule.lex(stripped);
    }

    @Override
    public Optional<String> generate(Node node) {
        return this.rule.generate(node);
    }
}