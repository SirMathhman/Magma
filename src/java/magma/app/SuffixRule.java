package magma.app;

import java.util.Optional;

public record SuffixRule(LastRule rule, String suffix) {
    Optional<Node> lex(String input) {
        if (!input.endsWith(this.suffix))
            return Optional.empty();

        final var withoutSuffix = input.substring(0, input.length() - this.suffix.length());
        return this.rule.lex(withoutSuffix);
    }
}