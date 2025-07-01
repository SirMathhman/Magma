package magma.rule;

public record StripRule<Node, StringResult, NodeResult>(Rule<Node, NodeResult, StringResult> rule)
        implements Rule<Node, NodeResult, StringResult> {
    @Override
    public NodeResult lex(final String input) {
        return this.rule.lex(input.strip());
    }

    @Override
    public StringResult generate(final Node node) {
        return this.rule.generate(node);
    }
}