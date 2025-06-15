package magma.app.rule;

import magma.app.Rule;
import magma.app.maybe.MaybeNode;
import magma.app.maybe.Prependable;
import magma.app.maybe.node.EmptyNode;

public record PrefixRule<Node, Generate extends Prependable<Generate>>(String prefix,
                                                                       Rule<Node, MaybeNode, Generate> rule) implements Rule<Node, MaybeNode, Generate> {
    @Override
    public Generate generate(Node node) {
        return this.rule.generate(node).prependString(this.prefix);
    }

    @Override
    public MaybeNode lex(String input) {
        if (!input.startsWith(this.prefix))
            return new EmptyNode();

        final var substring = input.substring(this.prefix.length());
        return this.rule.lex(substring);
    }
}