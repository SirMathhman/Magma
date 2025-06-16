package magma.app.compile.rule;

import magma.app.compile.error.CompileResultFactory;
import magma.app.compile.error.NodeListResult;
import magma.app.compile.error.StringResult;

public final class LastRule<Node, Error, NodeResult> implements Rule<Node, Error, NodeResult> {
    private final Rule<Node, Error, NodeResult> leftRule;
    private final String infix;
    private final Rule<Node, Error, NodeResult> rightRule;
    private final CompileResultFactory<Node, Error, StringResult<Error>, NodeResult, NodeListResult<Node, Error, NodeResult>> resultFactory;

    public LastRule(Rule<Node, Error, NodeResult> leftRule, String infix, Rule<Node, Error, NodeResult> rightRule, CompileResultFactory<Node, Error, StringResult<Error>, NodeResult, NodeListResult<Node, Error, NodeResult>> resultFactory) {
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
    public StringResult<Error> generate(Node node) {
        return this.leftRule.generate(node)
                .appendSlice(this.infix)
                .appendResult(() -> this.rightRule.generate(node));
    }
}