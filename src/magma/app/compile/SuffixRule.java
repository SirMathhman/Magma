package magma.app.compile;

import java.util.Optional;

public record SuffixRule(Rule child, String suffix) implements Rule {
    @Override
    public Optional<Node> parse(String input) {
        if(input.endsWith(suffix)) return child.parse(input.substring(0, input.length() - suffix.length()));
        return Optional.empty();
    }

    @Override
    public Optional<String> generate(Node node) {
        return child().generate(node).map(value -> value + suffix());
    }
}