package magma.app.compile.rule;

import magma.api.result.Result;
import magma.app.compile.error.CompileErrors;
import magma.app.compile.error.FormattedError;

public final class SuffixRule<Node> implements Rule<Node, Result<Node, FormattedError>, Result<String, FormattedError>> {
    private final Rule<Node, Result<Node, FormattedError>, Result<String, FormattedError>> rule;
    private final String suffix;

    public SuffixRule(Rule<Node, Result<Node, FormattedError>, Result<String, FormattedError>> rule, String suffix) {
        this.rule = rule;
        this.suffix = suffix;
    }

    @Override
    public Result<Node, FormattedError> lex(String input) {
        if (!input.endsWith(this.suffix))
            return CompileErrors.fromStringErr("Suffix '" + this.suffix + "' not present", input);

        final var withoutEnd = input.substring(0, input.length() - this.suffix.length());
        return this.rule.lex(withoutEnd);
    }

    @Override
    public Result<String, FormattedError> generate(Node node) {
        return this.rule.generate(node)
                .mapValue(result -> result + this.suffix);
    }
}