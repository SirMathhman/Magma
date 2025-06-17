package magma.app.compile.rule;

import magma.api.result.Err;
import magma.api.result.Result;
import magma.app.compile.context.StringContext;
import magma.app.compile.error.CompileError;
import magma.app.compile.error.FormattedError;

public record PrefixRule<Node>(String prefix,
                               Rule<Node, Result<Node, FormattedError>, Result<String, FormattedError>> rule) implements Rule<Node, Result<Node, FormattedError>, Result<String, FormattedError>> {
    @Override
    public Result<Node, FormattedError> lex(String input) {
        if (!input.startsWith(this.prefix))
            return new Err<>(new CompileError("Prefix '" + this.prefix + "' not present", new StringContext(input)));

        final var slice = input.substring(this.prefix.length());
        return this.rule()
                .lex(slice);
    }

    @Override
    public Result<String, FormattedError> generate(Node node) {
        return this.rule.generate(node)
                .mapValue(result -> this.prefix + result);
    }
}