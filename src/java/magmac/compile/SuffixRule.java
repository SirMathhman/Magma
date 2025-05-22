package magmac.compile;

import java.util.Optional;

public record SuffixRule(
        String suffix,
        Rule rule
) implements Rule {
    @Override
    public Optional<MapNode> apply(String input) {
        if (input.endsWith(this.suffix())) {
            var slice = input.substring(0, input.length() - this.suffix().length());
            return this.rule().apply(slice);
        }

        return Optional.empty();
    }
}