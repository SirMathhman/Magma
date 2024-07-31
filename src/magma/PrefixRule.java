package magma;

import java.util.Optional;

public record PrefixRule(String prefix, Rule child) implements Rule {
    @Override
    public Optional<Node> parse(String input) {
        if (!input.startsWith(prefix())) return Optional.empty();
        var truncatedRight = input.substring(prefix().length());
        return child().parse(truncatedRight);
    }

    @Override
    public Optional<String> generate(Node node) {
        return child.generate(node).map(inner -> prefix + inner);
    }
}