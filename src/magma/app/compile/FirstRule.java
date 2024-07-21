package magma.app.compile;

import magma.api.Tuple;
import magma.app.compile.rule.Rule;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public record FirstRule(Rule left, String slice, Rule right) implements Rule {
    static Optional<Tuple<String, String>> splitFirst(String slice, String input) {
        var index = input.indexOf(slice);
        if (index == -1) return Optional.empty();
        var left = input.substring(0, index);
        var right = input.substring(index + slice.length());
        return Optional.of(new Tuple<>(left, right));
    }

    private static HashMap<String, String> merge(Map<String, String> withModifiers, Map<String, String> wthName) {
        var merged = new HashMap<>(withModifiers);
        merged.putAll(wthName);
        return merged;
    }

    @Override
    public Optional<Map<String, String>> parse(String input) {
        return splitFirst(slice(), input).flatMap(tuple -> left().parse(tuple.left()).flatMap(withModifiers -> right().parse(tuple.right())
                .map(wthName -> merge(withModifiers, wthName))));
    }

    @Override
    public Optional<String> generate(Map<String, String> node) {
        return left.generate(node).flatMap(leftValue -> right().generate(node).map(rightValue -> leftValue + slice + rightValue));
    }
}