package magma.app.compile.rule;

import java.util.Map;
import java.util.Optional;

public record StripRule(String left, Rule child, String right) implements Rule {
    @Override
    public Optional<Map<String, String>> parse(String input) {
        return child.parse(input.strip());
    }

    @Override
    public Optional<String> generate(Map<String, String> node) {
        var leftSlice = node.getOrDefault(left, "");
        var rightSlice = node.getOrDefault(right, "");
        return child.generate(node).map(inner -> leftSlice + inner + rightSlice);
    }
}
