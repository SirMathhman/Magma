package magma.app.compile;

import magma.app.compile.rule.Rule;

import java.util.Map;
import java.util.Optional;

public record LeftRule(String slice, Rule child) implements Rule {
    @Override
    public Optional<Map<String, String>> parse(String input) {
        if (!input.startsWith(slice)) return Optional.empty();
        return child.parse(input.substring(slice.length()));
    }

    @Override
    public Optional<String> generate(Map<String, String> node) {
        return child.generate(node).map(inner -> slice + inner);
    }
}