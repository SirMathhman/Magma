package magma.app.compile.rule;

import magma.app.compile.node.Node;

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
                .flatMap(leftResult -> this.rightRule.generate(node)
                        .map(rightResult -> leftResult + this.infix + rightResult));
    }
}