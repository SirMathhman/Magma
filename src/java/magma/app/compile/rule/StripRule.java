package magma.app.compile.rule;

public record StripRule<Node, NodeResult, StringResult>(
        Rule<Node, NodeResult, StringResult> rule) implements Rule<Node, NodeResult, StringResult> {
    @Override
    public NodeResult lex(final String input) {
        return rule.lex(input.strip());
    }

    @Override
    public StringResult generate(final Node node) {
        return rule.generate(node);
    }
}