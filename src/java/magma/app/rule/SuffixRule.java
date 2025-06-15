package magma.app.rule;

import magma.app.Rule;
import magma.app.maybe.MaybeNode;
import magma.app.maybe.MaybeString;
import magma.app.maybe.node.EmptyNode;

public record SuffixRule<Node>(Rule<Node> rule, String suffix) implements Rule<Node> {
    @Override
    public MaybeString generate(Node node) {
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