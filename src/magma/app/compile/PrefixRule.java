package magma.app.compile;

import java.util.Optional;

public record PrefixRule(String slice, Rule child) implements Rule {
    @Override
    public Optional<Node> parse(String input) {
        if (!input.startsWith(slice())) return Optional.empty();
        var afterType = input.substring(slice().length());
        return child().parse(afterType);
    }

    @Override
    public Optional<String> generate(Node node) {
        return child.generate(node).map(inner -> slice + inner);
    }
}