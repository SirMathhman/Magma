package magma.app.rule;

import magma.app.Node;
import magma.app.Rule;
import magma.app.maybe.MaybeNode;
import magma.app.maybe.MaybeString;
import magma.app.maybe.node.EmptyNode;

public record PrefixRule(String prefix, Rule<Node> rule) implements Rule<Node> {
    @Override
    public MaybeString generate(Node node) {
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