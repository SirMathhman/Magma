package magma.app.rule;

import magma.app.Node;
import magma.app.Rule;

import java.util.Optional;

public record LastRule(Rule leftRule, String infix, Rule rightRule) implements Rule {
    @Override
    public Optional<Node> lex(String input) {
        final var separator = input.lastIndexOf(this.infix);
        if (separator < 0)
            return Optional.empty();

        final var rightResult = input.substring(separator + this.infix.length());
        return this.rightRule.lex(rightResult);
    }

    @Override
    public Optional<String> generate(Node node) {
        return this.leftRule.generate(node)
                .flatMap(leftResult -> {
                    return this.rightRule.generate(node)
                            .map(rightResult -> {
                                return leftResult + this.infix + rightResult;
                            });
                });
    }
}