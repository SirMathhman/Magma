package magma.app.compile.rule;

public record StripRule<Node, Lex, Generate>(
        Rule<magma.app.compile.node.Node, Lex, Generate> rule) implements Rule<magma.app.compile.node.Node, Lex, Generate> {
    @Override
    public Lex lex(String segment) {
        return this.rule.lex(segment.strip());
    }

    @Override
    public Generate generate(magma.app.compile.node.Node node) {
        return this.rule.generate(node);
    }
}