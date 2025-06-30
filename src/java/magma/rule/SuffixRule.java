package magma.rule;

import magma.node.EverythingNode;

import java.util.Optional;

public record SuffixRule(Rule<EverythingNode> rule, String suffix) implements Rule<EverythingNode> {
    @Override
    public Optional<EverythingNode> lex(final String input) {
        final var length = input.length();
        if (!input.endsWith(this.suffix)) return Optional.empty();
        final var suffixLength = this.suffix.length();
        final var substring = input.substring(0, length - suffixLength);
        return this.rule().lex(substring);
    }

    @Override
    public Optional<String> generate(final EverythingNode node) {
        return this.rule.generate(node).map(result -> this.suffix + result);
    }
}