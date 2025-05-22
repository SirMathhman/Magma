package magmac.compile;

import java.util.Optional;

public record InfixRule(
        Rule leftRule,
        String infix,
        Rule rightRule
) implements Rule {
    @Override
    public Optional<MapNode> parse(String input) {
        var index = input.indexOf(this.infix);
        if (0 > index) {
            return Optional.empty();
        }

        var leftString = input.substring(0, index);
        var rightString = input.substring(index + this.infix.length());
        return this.leftRule.parse(leftString).flatMap(leftValue -> {
            return this.rightRule.parse(rightString).map(rightValue -> {
                return leftValue.merge(rightValue);
            });
        });
    }

    @Override
    public Optional<String> generate(MapNode node) {
        return this.leftRule.generate(node).flatMap(leftString -> {
            return this.rightRule.generate(node).map(rightString -> {
                return leftString + this.infix + rightString;
            });
        });
    }
}