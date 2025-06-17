package magma.app.compile.rule;

import magma.api.result.Result;
import magma.app.compile.error.ResultFactory;

public final class PrefixRule<Node, Error> implements Rule<Node, Result<Node, Error>, Result<String, Error>> {
    private final String prefix;
    private final Rule<Node, Result<Node, Error>, Result<String, Error>> rule;
    private final ResultFactory<Node, Result<Node, Error>, Result<String, Error>> factory;

    public PrefixRule(String prefix, Rule<Node, Result<Node, Error>, Result<String, Error>> rule, ResultFactory<Node, Result<Node, Error>, Result<String, Error>> factory) {
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
    public Result<String, Error> generate(Node node) {
        return this.rule.generate(node)
                .mapValue(result -> this.prefix + result);
    }
}