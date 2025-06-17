package magma.app.compile.rule;

import magma.api.result.Err;
import magma.api.result.Result;
import magma.app.compile.context.StringContext;
import magma.app.compile.error.CompileError;
import magma.app.compile.error.FormattedError;

public final class SuffixRule<Node> implements Rule<Node> {
    private final Rule<Node> rule;
    private final String suffix;

    public SuffixRule(Rule<Node> rule, String suffix) {
        this.rule = rule;
        this.suffix = suffix;
    }

    @Override
    public Result<Node, FormattedError> lex(String input) {
        if (!input.endsWith(this.suffix))
            return new Err<>(new CompileError("Suffix '" + this.suffix + "' not present", new StringContext(input)));

        final var withoutEnd = input.substring(0, input.length() - this.suffix.length());
        return this.rule.lex(withoutEnd);
    }

    @Override
    public Result<String, FormattedError> generate(Node node) {
        return this.rule.generate(node)
                .mapValue(result -> result + this.suffix);
    }
}