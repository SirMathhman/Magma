package magma;

import java.util.Optional;

public record PrefixRule(Rule child, String prefix) implements Rule {
    @Override
    public Optional<Node> parse(String input) {
        if (!input.startsWith(prefix())) return Optional.empty();
        var truncatedRight = input.substring(prefix().length());
        return child().parse(truncatedRight);
    }
}