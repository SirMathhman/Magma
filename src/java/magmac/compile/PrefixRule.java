package magmac.compile;

import java.util.Optional;

public record PrefixRule(
        String prefix, Rule rule
) implements Rule {
    @Override
    public Optional<MapNode> parse(String input) {
        if (input.startsWith(this.prefix())) {
            var slice = input.substring(this.prefix().length());
            return this.rule().parse(slice);
        }

        return Optional.empty();
    }

    @Override
    public Optional<String> generate(MapNode node) {
        return this.rule.generate(node).map(inner -> this.prefix + inner);
    }
}