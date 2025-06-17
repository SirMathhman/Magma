package magma.app.compile.rule;

import magma.api.result.Err;
import magma.api.result.Ok;
import magma.api.result.Result;
import magma.app.compile.error.ResultFactory;

public final class SuffixRule<Node, Error, NodeResult> implements Rule<Node, NodeResult, Result<String, Error>> {
    private final Rule<Node, NodeResult, Result<String, Error>> rule;
    private final String suffix;
    private final ResultFactory<Node, NodeResult, Result<String, Error>> factory;

    public SuffixRule(Rule<Node, NodeResult, Result<String, Error>> rule, String suffix, ResultFactory<Node, NodeResult, Result<String, Error>> factory) {
        this.rule = rule;
        this.suffix = suffix;
        this.factory = factory;
    }

    @Override
    public NodeResult lex(String input) {
        if (!input.endsWith(this.suffix))
            return this.factory.fromStringErr("Suffix '" + this.suffix + "' not present", input);

        final var withoutEnd = input.substring(0, input.length() - this.suffix.length());
        return this.rule.lex(withoutEnd);
    }

    @Override
    public Result<String, Error> generate(Node node) {
        Result<String, Error> stringErrorResult = this.rule.generate(node);
        return switch (stringErrorResult) {
            case Err<String, Error>(Error error) -> new Err<>(error);
            case Ok<String, Error>(
                    String value
            ) -> new Ok<>(value + this.suffix);
        };
    }
}