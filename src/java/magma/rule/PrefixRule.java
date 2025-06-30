package magma.rule;

import magma.node.Node;

import java.util.Optional;

public record PrefixRule(String prefix, Rule rule) implements Rule {
    @Override
    public Optional<Node> lex(final String input) {
        if (!input.startsWith(this.prefix)) return Optional.empty();
        final var prefixLength = this.prefix.length();

        final var substring1 = input.substring(prefixLength);
        return this.rule().lex(substring1);
    }

    @Override
    public Optional<String> generate(final Node node) {
        return this.rule.generate(node).map(result -> this.prefix + result);
    }
}