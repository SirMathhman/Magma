package magma.app.rule;

import magma.app.node.Node;

import java.util.Optional;

public record InfixRule(Rule leftRule, String infix, Rule rightRule) implements Rule {
    @Override
    public Optional<Node> lex(String input) {
        final var index = input.indexOf(this.infix);
        if (index == -1)
            return Optional.empty();

        final var left = input.substring(0, index);
        final var right = input.substring(index + this.infix.length());
        return this.leftRule.lex(left)
                .flatMap(leftResult -> this.rightRule.lex(right)
                        .map(leftResult::merge));
    }

    @Override
    public Optional<String> generate(Node node) {
        return this.leftRule.generate(node)
                .flatMap(leftResult -> {
                    return this.rightRule.generate(node)
                            .map(rightResult -> leftResult + this.infix + rightResult);
                });
    }
}
