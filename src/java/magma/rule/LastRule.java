package magma.rule;

import magma.node.result.NodeErr;
import magma.node.result.NodeResult;

public record LastRule(String infix, Rule rightRule) implements Rule {
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
}