package magmac.compile;

import java.util.Optional;

public record SuffixRule(
        Rule rule, String suffix
) implements Rule {
    @Override
    public Optional<MapNode> apply(String input) {
        if (input.endsWith(this.suffix())) {
            var slice = input.substring(0, input.length() - this.suffix().length());
            return this.rule().apply(slice);
        }

        return Optional.empty();
    }

    @Override
    public Optional<String> generate(MapNode node) {
        return this.rule.generate(node).map(inner -> inner + this.suffix);
    }
}