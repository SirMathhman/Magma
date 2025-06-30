package magma.rule;

import magma.node.Node;

import java.util.Optional;

public record SuffixRule(Rule rule, String suffix) implements Rule {
    @Override
    public Optional<Node> lex(final String input) {
        final var length = input.length();
        if (!input.endsWith(this.suffix)) return Optional.empty();
        final var suffixLength = this.suffix.length();
        final var substring = input.substring(0, length - suffixLength);
        return this.rule().lex(substring);
    }
}