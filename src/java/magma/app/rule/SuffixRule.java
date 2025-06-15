package magma.app.rule;

import magma.app.Rule;
import magma.app.maybe.string.Appendable;
import magma.app.maybe.MaybeNode;
import magma.app.maybe.node.EmptyNode;

public record SuffixRule<Node, Generate extends Appendable<Generate>>(Rule<Node, MaybeNode, Generate> rule,
                                                                      String suffix) implements Rule<Node, MaybeNode, Generate> {
    @Override
    public Generate generate(Node node) {
        return this.rule.generate(node).appendString(this.suffix);
    }

    @Override
    public MaybeNode lex(String input) {
        if (!input.endsWith(this.suffix))
            return new EmptyNode();

        return this.rule.lex(input.substring(0, input.length() - this.suffix.length()));
    }
}