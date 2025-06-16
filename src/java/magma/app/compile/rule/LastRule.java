package magma.app.compile.rule;

import magma.app.compile.error.CompileResultFactory;
import magma.app.compile.error.NodeListResult;
import magma.app.compile.error.NodeResult;
import magma.app.compile.error.StringResult;

public final class LastRule<Node> implements Rule<Node> {
    private final Rule<Node> leftRule;
    private final String infix;
    private final Rule<Node> rightRule;
    private final CompileResultFactory<Node, StringResult, NodeResult<Node>, NodeListResult<Node>> resultFactory;

    public LastRule(Rule<Node> leftRule, String infix, Rule<Node> rightRule, CompileResultFactory<Node, StringResult, NodeResult<Node>, NodeListResult<Node>> resultFactory) {
        this.leftRule = leftRule;
        this.infix = infix;
        this.rightRule = rightRule;
        this.resultFactory = resultFactory;
    }

    @Override
    public NodeResult<Node> lex(String input) {
        final var separator = input.lastIndexOf(this.infix);
        if (separator < 0)
            return this.resultFactory.fromStringError("Infix '" + this.infix + "' not present", input);

        final var rightResult = input.substring(separator + this.infix.length());
        return this.rightRule.lex(rightResult);
    }

    @Override
    public StringResult generate(Node node) {
        return this.leftRule.generate(node)
                .appendSlice(this.infix)
                .appendResult(() -> this.rightRule.generate(node));
    }
}