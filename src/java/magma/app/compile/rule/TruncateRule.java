package magma.app.compile.rule;

import magma.app.compile.error.CompileResultFactory;
import magma.app.compile.error.NodeListResult;
import magma.app.compile.error.NodeResult;
import magma.app.compile.error.StringResult;
import magma.app.compile.rule.truncate.Truncator;

public final class TruncateRule<Node> implements Rule<Node> {
    private final Rule<Node> rule;
    private final Truncator truncator;
    private final CompileResultFactory<Node, StringResult, NodeResult<Node>, NodeListResult<Node>> resultFactory;

    public TruncateRule(Rule<Node> rule, Truncator truncator, CompileResultFactory<Node, StringResult, NodeResult<Node>, NodeListResult<Node>> resultFactory) {
        this.rule = rule;
        this.truncator = truncator;
        this.resultFactory = resultFactory;
    }

    @Override
    public NodeResult<Node> lex(String input) {
        return this.truncator.truncate(input)
                .map(this.rule::lex)
                .orElseGet(() -> this.resultFactory.fromStringError(this.truncator.createErrorMessage(), input));
    }

    @Override
    public StringResult generate(Node node) {
        return this.rule.generate(node)
                .complete(this.truncator::complete);
    }
}