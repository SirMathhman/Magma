package magma;

import java.util.Optional;

public record FirstRule(Rule leftRule, String slice, Rule rightRule) implements Rule {
    @Override
    public Optional<Node> parse(String input) {
        var startIndex = input.indexOf(slice());
        if (startIndex == -1) return Optional.empty();
        var leftSlice = input.substring(0, startIndex);

        var leftResult = leftRule().parse(leftSlice);
        if (leftResult.isEmpty()) return Optional.empty();

        var rightSlice = input.substring(startIndex + slice.length());
        var withContentOptional = rightRule().parse(rightSlice);

        return withContentOptional.map(node -> leftResult.get().merge(node));
    }

    @Override
    public Optional<String> generate(Node node) {
        throw new UnsupportedOperationException();
    }
}