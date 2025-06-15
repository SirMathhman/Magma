package magma.app.rule;

import magma.app.Rule;
import magma.app.maybe.MaybeNodeList;
import magma.app.maybe.string.Appendable;
import magma.app.maybe.MaybeNode;
import magma.app.maybe.node.EmptyNode;

public record InfixRule<Node, Generate extends Appendable<Generate>>(Rule<Node, MaybeNode<MaybeNodeList>, Generate> leftRule,
                                                                     String infix,
                                                                     Rule<Node, MaybeNode<MaybeNodeList>, Generate> rightRule) implements Rule<Node, MaybeNode<MaybeNodeList>, Generate> {

    @Override
    public Generate generate(Node node) {
        return this.leftRule.generate(node).appendString(this.infix).appendMaybe(this.rightRule.generate(node));
    }

    @Override
    public MaybeNode<MaybeNodeList> lex(String input) {
        final var index = input.lastIndexOf(this.infix);
        if (index < 0)
            return new EmptyNode();

        final var destination = input.substring(index + this.infix.length());
        return this.rightRule.lex(destination);
    }
}