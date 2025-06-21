package magma.app;

import magma.app.node.Node;

import java.util.Optional;

public record SuffixRule(Rule child, String suffix) implements Rule {
    @Override
    public Optional<Node> lex(final String input) {
        if (!input.endsWith(this.suffix))
            return Optional.empty();

        final var inputLength = input.length();
        final var suffixLength = this.suffix.length();
        final var slice = input.substring(0, inputLength - suffixLength);
        return this.child.lex(slice);
    }
}