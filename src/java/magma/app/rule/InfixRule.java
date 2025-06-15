package magma.app.rule;

import magma.app.Node;
import magma.app.Rule;
import magma.app.maybe.Appendable;
import magma.app.maybe.MaybeNode;
import magma.app.maybe.node.EmptyNode;

public record InfixRule<Generate extends Appendable<Generate>>(Rule<Node, MaybeNode, Generate> leftRule, String infix,
                                                               Rule<Node, MaybeNode, Generate> rightRule) implements Rule<Node, MaybeNode, Generate> {

    @Override
    public Generate generate(Node node) {
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

    @Override
    public String toString() {
        return "InfixRule[" + "leftRule=" + this.leftRule + ", " + "infix=" + this.infix + ", " + "rightRule=" + this.rightRule + ']';
    }

}