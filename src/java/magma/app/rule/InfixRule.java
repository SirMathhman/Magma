package magma.app.rule;

import magma.app.maybe.MaybeNode;
import magma.app.Rule;
import magma.app.maybe.node.EmptyNode;

public record InfixRule(String infix, Rule rule) implements Rule {
    @Override
    public MaybeNode lex(String input) {
        final var index = input.lastIndexOf(this.infix);
        if (index < 0)
            return new EmptyNode();

        final var destination = input.substring(index + this.infix.length());
        return this.rule.lex(destination);
    }
}