package magma.app.compile.rule;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public record OrRule(List<Rule> children) implements Rule {
    @Override
    public Optional<Map<String, String>> parse(String input) {
        for (Rule child : children) {
            var result = child.parse(input);
            if (result.isPresent()) return result;
        }

        return Optional.empty();
    }

    @Override
    public Optional<String> generate(Map<String, String> node) {
        for (Rule child : children) {
            var result = child.generate(node);
            if (result.isPresent()) return result;
        }

        return Optional.empty();
    }
}
