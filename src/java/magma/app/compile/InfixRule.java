package magma.app.compile;

public final class InfixRule<Node, Error, NodeResult extends MergeNodeResult<Node, NodeResult>, StringResult extends AppendableStringResult<StringResult>> implements
        Rule<Node, NodeResult, StringResult> {
    private final Rule<Node, NodeResult, StringResult> leftRule;
    private final String infix;
    private final Rule<Node, NodeResult, StringResult> rightRule;
    private final ResultFactory<Node, Error, NodeResult, StringResult> factory;

    public InfixRule(Rule<Node, NodeResult, StringResult> leftRule, String infix, Rule<Node, NodeResult, StringResult> rightRule, ResultFactory<Node, Error, NodeResult, StringResult> factory) {
        this.leftRule = leftRule;
        this.infix = infix;
        this.rightRule = rightRule;
        this.factory = factory;
    }

    @Override
    public NodeResult lex(String input) {
        final var index = input.indexOf(this.infix);
        if (index == -1)
            return this.factory.fromStringErr("Infix '" + this.infix + "' not present", input);

        final var left = input.substring(0, index);
        final var right = input.substring(index + this.infix.length());
        return this.getNodeResult(this.leftRule.lex(left), right);
    }

    private NodeResult getNodeResult(NodeResult leftResult, String slice) {
        return leftResult.mergeResult(() -> this.rightRule.lex(slice));
    }

    @Override
    public StringResult generate(Node node) {
        return this.appendInfix(node, this.leftRule.generate(node));
    }

    private StringResult appendInfix(Node node, StringResult result) {
        return this.appendRight(node, result.appendSlice(this.infix));
    }

    private StringResult appendRight(Node node, StringResult result1) {
        return result1.appendResult(() -> this.rightRule.generate(node));
    }
}
