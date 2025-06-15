package magma.app.rule;

import magma.app.Node;
import magma.app.Rule;
import magma.app.maybe.MaybeNode;
import magma.app.maybe.MaybeString;
import magma.app.maybe.node.EmptyNode;

public record InfixRule(Rule<Node, MaybeString> leftRule, String infix, Rule<Node, MaybeString> rightRule) implements Rule<Node, MaybeString> {
    @Override
    public MaybeString generate(Node node) {
        return this.leftRule.generate(node).appendString(this.infix).appendMaybe(this.rightRule.generate(node));
    }

    @Override
    public MaybeNode lex(String input) {
        final var index = input.lastIndexOf(this.infix);
        if (index < 0)
            return new EmptyNode();

        final var destination = input.substring(index + this.infix.length());
        return this.rightRule.lex(destination);
    }
}