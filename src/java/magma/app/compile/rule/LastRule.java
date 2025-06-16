package magma.app.compile.rule;

import magma.app.compile.error.CompileResultFactory;
import magma.app.compile.error.NodeListResult;
import magma.app.compile.error.NodeResult;
import magma.app.compile.error.StringResult;

public final class LastRule<Node, Error> implements Rule<Node, Error, NodeResult<Node, Error>> {
    private final Rule<Node, Error, NodeResult<Node, Error>> leftRule;
    private final String infix;
    private final Rule<Node, Error, NodeResult<Node, Error>> rightRule;
    private final CompileResultFactory<Node, Error, StringResult<Error>, NodeResult<Node, Error>, NodeListResult<Node, Error>> resultFactory;

    public LastRule(Rule<Node, Error, NodeResult<Node, Error>> leftRule, String infix, Rule<Node, Error, NodeResult<Node, Error>> rightRule, CompileResultFactory<Node, Error, StringResult<Error>, NodeResult<Node, Error>, NodeListResult<Node, Error>> resultFactory) {
        this.leftRule = leftRule;
        this.infix = infix;
        this.rightRule = rightRule;
        this.resultFactory = resultFactory;
    }

    @Override
    public NodeResult<Node, Error> lex(String input) {
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