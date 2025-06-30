package magma.rule;

import magma.node.Node;

import java.util.Optional;

public record SuffixRule(Rule rule, String suffix) implements Rule {
    @Override
    public Optional<Node> lex(final String strip) {
        final var length = strip.length();
        if (strip.endsWith(this.suffix())) {
            final var suffixLength = this.suffix().length();
            final var substring = strip.substring(0, length - suffixLength);
            return this.rule().lex(substring);
        } else return Optional.empty();
    }
}