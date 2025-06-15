package magma.app.rule;

import magma.app.Node;
import magma.app.Rule;

import java.util.Optional;

public record SuffixRule(Rule rule, String suffix) implements Rule {
    @Override
    public Optional<Node> lex(String input) {
        if (!input.endsWith(this.suffix))
            return Optional.empty();

        final var withoutSuffix = input.substring(0, input.length() - this.suffix.length());
        return this.rule.lex(withoutSuffix);
    }

    @Override
    public Optional<String> generate(Node node) {
        return this.rule.generate(node)
                .map(result -> result + this.suffix);
    }
}