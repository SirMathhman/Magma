package magma.app.compile.rule;

import magma.app.compile.node.NodeWithEverything;

import java.util.Optional;

public record LastRule(Rule<NodeWithEverything> leftRule, String infix, Rule<NodeWithEverything> rightRule) implements Rule<NodeWithEverything> {
    @Override
    public Optional<NodeWithEverything> lex(String input) {
        final var separator = input.lastIndexOf(this.infix);
        if (separator < 0)
            return Optional.empty();

        final var rightResult = input.substring(separator + this.infix.length());
        return this.rightRule.lex(rightResult);
    }

    @Override
    public Optional<String> generate(NodeWithEverything node) {
        return this.leftRule.generate(node)
                .flatMap(leftResult -> this.rightRule.generate(node)
                        .map(rightResult -> leftResult + this.infix + rightResult));
    }
}