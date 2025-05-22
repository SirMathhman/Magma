package magmac.compile;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public record OrRule(List<Rule> rules) implements Rule {
    @Override
    public Optional<MapNode> parse(String input) {
        return this.or(rule -> rule.parse(input));
    }

    private <T> Optional<T> or(Function<Rule, Optional<T>> mapper) {
        return this.rules.stream()
                .map(mapper)
                .flatMap(Optional::stream)
                .findFirst();
    }

    @Override
    public Optional<String> generate(MapNode node) {
        return this.or(rule -> rule.generate(node));
    }
}
