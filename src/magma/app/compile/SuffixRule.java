package magma.app.compile;

import java.util.Optional;

public record SuffixRule(Rule child, String suffix) implements Rule {
    @Override
    public Optional<String> generate(Node node) {
        return child().generate(node).map(value -> value + suffix());
    }
}