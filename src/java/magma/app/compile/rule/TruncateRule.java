package magma.app.compile.rule;

import magma.app.compile.error.CompileResultFactory;
import magma.app.compile.error.Completable;
import magma.app.compile.error.NodeListResult;
import magma.app.compile.rule.truncate.Truncator;

public final class TruncateRule<Node, Error, NodeResult, StringResult extends Completable<Error, StringResult>> implements Rule<Node, NodeResult, StringResult> {
    private final Rule<Node, NodeResult, StringResult> rule;
    private final Truncator truncator;
    private final CompileResultFactory<Node, Error, StringResult, NodeResult, NodeListResult<Node, NodeResult>> resultFactory;

    public TruncateRule(Rule<Node, NodeResult, StringResult> rule, Truncator truncator, CompileResultFactory<Node, Error, StringResult, NodeResult, NodeListResult<Node, NodeResult>> resultFactory) {
        this.rule = rule;
        this.truncator = truncator;
        this.resultFactory = resultFactory;
    }

    @Override
    public NodeResult lex(String input) {
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