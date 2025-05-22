package magmac.compile;

import java.util.Optional;

public record InfixRule(
        Rule leftRule,
        String infix,
        Rule rightRule
) implements Rule {
    @Override
    public Optional<MapNode> apply(String input) {
        var index = input.indexOf(this.infix);
        if (0 > index) {
            return Optional.empty();
        }

        var leftString = input.substring(0, index);
        var rightString = input.substring(index + this.infix.length());
        return this.leftRule.apply(leftString).flatMap(leftValue -> {
            return this.rightRule.apply(rightString).map(rightValue -> {
                return leftValue.merge(rightValue);
            });
        });
    }
}