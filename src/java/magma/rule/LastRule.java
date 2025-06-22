package magma.rule;

import magma.node.Node;
import magma.option.None;
import magma.option.Option;

public record LastRule(String infix, Rule rightRule) implements Rule {
    @Override
    public Option<Node> lex(final String input) {
        final var separator = input.lastIndexOf(this.infix());
        if (0 > separator)
            return new None<>();

        final var rightSlice = input.substring(separator + this.infix()
                .length());
        return this.rightRule()
                .lex(rightSlice);
    }
}