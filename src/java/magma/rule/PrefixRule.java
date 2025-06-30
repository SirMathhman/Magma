package magma.rule;

import magma.node.EverythingNode;

import java.util.Optional;

public record PrefixRule(String prefix, Rule<EverythingNode> rule) implements Rule<EverythingNode> {
    @Override
    public Optional<EverythingNode> lex(final String input) {
        if (!input.startsWith(this.prefix)) return Optional.empty();
        final var prefixLength = this.prefix.length();

        final var substring1 = input.substring(prefixLength);
        return this.rule().lex(substring1);
    }

    @Override
    public Optional<String> generate(final EverythingNode node) {
        return this.rule.generate(node).map(result -> this.prefix + result);
    }
}