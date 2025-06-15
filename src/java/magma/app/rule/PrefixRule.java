package magma.app.rule;

import magma.app.Node;
import magma.app.Rule;

import java.util.Optional;

public record PrefixRule(String prefix, Rule rule) implements Rule {
    @Override
    public Optional<Node> lex(String input) {
        if (!input.startsWith(this.prefix))
            return Optional.empty();

        final var withoutPrefix = input.substring(this.prefix.length());
        return this.rule.lex(withoutPrefix);
    }
}