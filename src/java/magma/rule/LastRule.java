package magma.rule;

import magma.node.Node;
import magma.node.result.NodeErr;
import magma.node.result.NodeResult;
import magma.option.Option;

public record LastRule(Rule leftRule, String infix, Rule rightRule) implements Rule {
    @Override
    public NodeResult lex(final String input) {
        final var separator = input.lastIndexOf(this.infix());
        if (0 > separator)
            return new NodeErr();

        final var rightSlice = input.substring(separator + this.infix()
                .length());
        return this.rightRule()
                .lex(rightSlice);
    }

    @Override
    public Option<String> generate(final Node node) {
        return this.leftRule.generate(node)
                .flatMap(leftSlice -> this.rightRule.generate(node)
                        .map(rightSlice -> leftSlice + this.infix + rightSlice));
    }
}