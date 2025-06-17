package magma.app.compile.rule;

import magma.api.result.Result;
import magma.app.compile.error.FormattedError;
import magma.app.compile.error.ResultFactory;

public final class SuffixRule<Node> implements Rule<Node, Result<Node, FormattedError>, Result<String, FormattedError>> {
    private final Rule<Node, Result<Node, FormattedError>, Result<String, FormattedError>> rule;
    private final String suffix;
    private final ResultFactory<Node, FormattedError> factory;

    public SuffixRule(Rule<Node, Result<Node, FormattedError>, Result<String, FormattedError>> rule, String suffix, ResultFactory<Node, FormattedError> factory) {
        this.rule = rule;
        this.suffix = suffix;
        this.factory = factory;
    }

    @Override
    public Result<Node, FormattedError> lex(String input) {
        if (!input.endsWith(this.suffix))
            return this.factory.fromStringErr("Suffix '" + this.suffix + "' not present", input);

        final var withoutEnd = input.substring(0, input.length() - this.suffix.length());
        return this.rule.lex(withoutEnd);
    }

    @Override
    public Result<String, FormattedError> generate(Node node) {
        return this.rule.generate(node)
                .mapValue(result -> result + this.suffix);
    }
}