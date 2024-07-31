package magma;

import java.util.Optional;

public record SuffixRule(Rule child, String suffix) implements Rule {
    @Override
    public Optional<Node> parse(String input) {
        if (!input.endsWith(suffix())) return Optional.empty();
        var name = input.substring(0, input.length() - suffix().length());
        return child().parse(name);
    }
}