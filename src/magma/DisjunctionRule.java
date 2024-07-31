package magma;

import java.util.List;
import java.util.Optional;

public record DisjunctionRule(List<Rule> rules) implements Rule {
    @Override
    public Optional<Node> parse(String input) {
        for (Rule rule : rules) {
            var result = rule.parse(input);
            if (result.isPresent()) return result;
        }

        return Optional.empty();
    }
}
