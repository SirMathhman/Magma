package magma.app.compile.rule;

import java.util.Map;
import java.util.Optional;

public record ExtractRule(String propertyKey) implements Rule {
    private Optional<Map<String, String>> parse0(String input) {
        return Optional.of(Map.of(propertyKey(), input));
    }

    private Optional<String> generate0(Map<String, String> node) {
        return node.containsKey(propertyKey()) ? Optional.of(node.get(propertyKey())) : Optional.empty();
    }

    @Override
    public Optional<Node> parse(String input) {
        return parse0(input).map(strings -> new Node(Optional.empty(), strings));
    }

    @Override
    public Optional<String> generate(Node node) {
        return generate0(node.strings());
    }
}