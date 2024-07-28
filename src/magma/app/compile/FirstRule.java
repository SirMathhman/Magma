package magma.app.compile;

import magma.api.Optionals;

import java.util.Optional;

public record FirstRule(Rule left, String slice, Rule right) implements Rule {
    @Override
    public Optional<Node> parse(String input) {
        var index = input.indexOf(slice);
        if (index == -1) return Optional.empty();

        var leftSlice = input.substring(0, index);
        var rightSlice = input.substring(index + slice.length());

        return Optionals.and(left.parse(leftSlice), () -> right.parse(rightSlice)).map(tuple -> tuple.left().merge(tuple.right()));
    }

    @Override
    public Optional<String> generate(Node node) {
        return Optionals.and(left.generate(node), () -> right.generate(node)).map(tuple -> tuple.left() + slice + tuple.right());
    }
}