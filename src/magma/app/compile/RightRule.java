package magma.app.compile;

import magma.app.compile.rule.Rule;

import java.util.Map;
import java.util.Optional;

public record RightRule(Rule child, String slice) implements Rule {
    static Optional<String> truncateRight(String input, String slice) {
        if (!input.endsWith(slice)) return Optional.empty();
        return Optional.of(input.substring(0, input.length() - slice.length()));
    }

    @Override
    public Optional<Map<String, String>> parse(String input) {
        return truncateRight(input, slice()).flatMap(child()::parse);
    }
}