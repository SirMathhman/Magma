package magma.app.compile.rule;

import magma.app.compile.error.CompileResultFactory;
import magma.app.compile.error.NodeListResult;
import magma.app.compile.error.NodeResult;
import magma.app.compile.error.StringResult;
import magma.app.compile.rule.truncate.Truncator;

public final class TruncateRule<Node, Error> implements Rule<Node, Error> {
    private final Rule<Node, Error> rule;
    private final Truncator truncator;
    private final CompileResultFactory<Node, Error, StringResult<Error>, NodeResult<Node, Error>, NodeListResult<Node, Error>> resultFactory;

    public TruncateRule(Rule<Node, Error> rule, Truncator truncator, CompileResultFactory<Node, Error, StringResult<Error>, NodeResult<Node, Error>, NodeListResult<Node, Error>> resultFactory) {
        this.rule = rule;
        this.truncator = truncator;
        this.resultFactory = resultFactory;
    }

    @Override
    public NodeResult<Node, Error> lex(String input) {
        return this.truncator.truncate(input)
                .map(this.rule::lex)
                .orElseGet(() -> this.resultFactory.fromStringError(this.truncator.createErrorMessage(), input));
    }

    @Override
    public StringResult<Error> generate(Node node) {
        return this.rule.generate(node)
                .complete(this.truncator::complete);
    }
}