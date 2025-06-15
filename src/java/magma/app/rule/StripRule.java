package magma.app.rule;

import magma.app.Rule;

public record StripRule<Node, Lex, Generate>(Rule<Node, Lex, Generate> rule) implements Rule<Node, Lex, Generate> {
    @Override
    public Generate generate(Node node) {
        return this.rule.generate(node);
    }

    @Override
    public Lex lex(String input) {
        return this.rule.lex(input.strip());
    }
}