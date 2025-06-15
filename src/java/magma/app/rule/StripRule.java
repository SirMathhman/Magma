package magma.app.rule;

import magma.app.Node;
import magma.app.Rule;
import magma.app.maybe.MaybeNode;
import magma.app.maybe.MaybeString;

public record StripRule(Rule<Node, MaybeNode, MaybeString> rule) implements Rule<Node, MaybeNode, MaybeString> {
    @Override
    public MaybeString generate(Node node) {
        return this.rule.generate(node);
    }

    @Override
    public MaybeNode lex(String input) {
        final var strip = input.strip();
        return this.rule().lex(strip);
    }
}