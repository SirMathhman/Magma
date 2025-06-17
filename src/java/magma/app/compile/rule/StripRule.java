package magma.app.compile.rule;

public record StripRule<Node, Lex, Generate>(Rule<Node, Lex, Generate> rule) implements Rule<Node, Lex, Generate> {
    @Override
    public Lex lex(String segment) {
        return this.rule.lex(segment.strip());
    }

    @Override
    public Generate generate(Node node) {
        return this.rule.generate(node);
    }
}