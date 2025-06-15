package magma.app;

import java.util.Optional;

public record PrefixRule(String prefix, SuffixRule rule) {
    Optional<Node> lex(String input) {
        if (!input.startsWith(this.prefix))
            return Optional.empty();

        final var withoutPrefix = input.substring(this.prefix.length());
        return this.rule.lex(withoutPrefix);
    }
}