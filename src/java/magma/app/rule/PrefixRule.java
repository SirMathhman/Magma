package magma.app.rule;

import magma.app.Rule;
import magma.app.maybe.MaybeNode;
import magma.app.maybe.node.EmptyNode;

public record PrefixRule(String prefix, Rule rule) implements Rule {
    @Override
    public MaybeNode lex(String input) {
        if (!input.startsWith(this.prefix))
            return new EmptyNode();

        final var substring = input.substring(this.prefix.length());
        return this.rule.lex(substring);
    }
}