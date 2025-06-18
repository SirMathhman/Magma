package magma.app;

import java.util.Optional;

public record PrefixRule(String prefix, SuffixRule rule) {
    public Optional<Node> lex(String input) {
        if (!input.startsWith(this.prefix()))
            return Optional.empty();

        final var slice = input.substring(this.prefix()
                .length());
        return this.rule()
                .lex(slice);
    }
}