package magma.app.compile.rule;

import magma.app.compile.error.CompileResult;
import magma.app.compile.error.CompileResultFactory;
import magma.app.compile.rule.truncate.Truncator;

public final class TruncateRule<Node> implements Rule<Node> {
    private final Rule<Node> rule;
    private final Truncator truncator;
    private final CompileResultFactory<Node> resultFactory;

    public TruncateRule(Rule<Node> rule, Truncator truncator, CompileResultFactory<Node> resultFactory) {
        this.rule = rule;
        this.truncator = truncator;
        this.resultFactory = resultFactory;
    }

    @Override
    public CompileResult<Node> lex(String input) {
        return this.truncator.truncate(input)
                .map(this.rule::lex)
                .orElseGet(() -> this.resultFactory.fromStringError(this.truncator.createErrorMessage(), input));
    }

    @Override
    public CompileResult<String> generate(Node node) {
        return this.rule.generate(node)
                .mapValue(this.truncator::complete);
    }
}