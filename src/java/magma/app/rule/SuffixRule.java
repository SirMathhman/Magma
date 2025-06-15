package magma.app.rule;

import magma.app.Rule;
import magma.app.maybe.Appendable;
import magma.app.maybe.MaybeNode;
import magma.app.maybe.node.EmptyNode;

public record SuffixRule<Node, Generated extends Appendable<Generated>>(Rule<Node, Generated> rule,
                                                                        String suffix) implements Rule<Node, Generated> {
    @Override
    public Generated generate(Node node) {
        return this.rule.generate(node).appendString(this.suffix);
    }

    @Override
    public MaybeNode lex(String input) {
        if (!input.endsWith(this.suffix))
            return new EmptyNode();

        final var substring1 = input.substring(0, input.length() - this.suffix.length());
        return this.rule.lex(substring1);
    }
}