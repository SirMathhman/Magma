package magma.app.compile.rule;

import magma.app.compile.error.Appendable;
import magma.app.compile.error.CompileResultFactory;
import magma.app.compile.error.NodeListResult;

public final class LastRule<Node, Error, NodeResult, StringResult extends Appendable<StringResult>> implements Rule<Node, NodeResult, StringResult> {
    private final Rule<Node, NodeResult, StringResult> leftRule;
    private final String infix;
    private final Rule<Node, NodeResult, StringResult> rightRule;
    private final CompileResultFactory<Node, Error, StringResult, NodeResult, NodeListResult<Node, NodeResult>> resultFactory;

    public LastRule(Rule<Node, NodeResult, StringResult> leftRule, String infix, Rule<Node, NodeResult, StringResult> rightRule, CompileResultFactory<Node, Error, StringResult, NodeResult, NodeListResult<Node, NodeResult>> resultFactory) {
        this.leftRule = leftRule;
        this.infix = infix;
        this.rightRule = rightRule;
        this.resultFactory = resultFactory;
    }

    @Override
    public NodeResult lex(String input) {
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