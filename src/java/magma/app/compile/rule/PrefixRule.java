package magma.app.compile.rule;

import magma.api.result.Result;
import magma.app.compile.error.ResultFactory;
import magma.app.compile.error.StringErr;
import magma.app.compile.error.StringOk;
import magma.app.compile.error.StringResult;

public final class PrefixRule<Node, Error> implements Rule<Node, Result<Node, Error>, StringResult> {
    private final String prefix;
    private final Rule<Node, Result<Node, Error>, StringResult> rule;
    private final ResultFactory<Node, Result<Node, Error>, StringResult> factory;

    public PrefixRule(String prefix, Rule<Node, Result<Node, Error>, StringResult> rule, ResultFactory<Node, Result<Node, Error>, StringResult> factory) {
        this.prefix = prefix;
        this.rule = rule;
        this.factory = factory;
    }

    @Override
    public Result<Node, Error> lex(String input) {
        if (!input.startsWith(this.prefix))
            return this.factory.fromStringErr("Prefix '" + this.prefix + "' not present", input);

        final var slice = input.substring(this.prefix.length());
        return this.rule.lex(slice);
    }

    @Override
    public StringResult generate(Node node) {
        StringResult stringErrorResult = this.rule.generate(node);
        return switch (stringErrorResult) {
            case StringErr(var error) -> new StringErr(error);
            case StringOk(
                    String value
            ) -> new StringOk(this.prefix + value);
        };
    }
}