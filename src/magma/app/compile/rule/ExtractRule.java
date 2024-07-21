package magma.app.compile.rule;

import java.util.Map;
import java.util.Optional;

public record ExtractRule(String propertyKey) implements Rule {
    @Override
    public Optional<Map<String, String>> parse(String input) {
        return Optional.of(Map.of(propertyKey(), input));
    }

    @Override
    public Optional<String> generate(Map<String, String> node) {
        return node.containsKey(propertyKey()) ? Optional.of(node.get(propertyKey())) : Optional.empty();
    }
}