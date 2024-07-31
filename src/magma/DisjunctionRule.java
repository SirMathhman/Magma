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

    @Override
    public Optional<String> generate(Node node) {
        for (Rule rule : rules) {
            var generated = rule.generate(node);
            if(generated.isPresent()) return generated;
        }

        return Optional.empty();
    }
}
