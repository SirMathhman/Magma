package magma.app.compile;

import magma.api.Tuple;
import magma.app.compile.rule.Node;
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

    private Optional<Map<String, String>> parse0(String input) {
        return splitFirst(slice(), input).flatMap(tuple -> this.left().parse(tuple.left()).map(Node::strings).flatMap(withModifiers -> {
            return this.right().parse(tuple.right()).map(Node::strings)
                    .map(wthName -> merge(withModifiers, wthName));
        }));
    }

    private Optional<String> generate0(Map<String, String> node) {
        return left.generate(new Node(node)).flatMap(leftValue -> this.right().generate(new Node(node)).map(rightValue -> leftValue + slice + rightValue));
    }

    @Override
    public Optional<Node> parse(String input) {
        return parse0(input).map(Node::new);
    }

    @Override
    public Optional<String> generate(Node node) {
        return generate0(node.strings());
    }
}