package magma.app.compile.rule;

import magma.api.Err;
import magma.api.Ok;
import magma.app.compile.GenerateError;
import magma.app.compile.Node;
import magma.app.compile.ParseError;

public class EmptyRule implements Rule {
    public static final Rule EMPTY_RULE = new EmptyRule();

    private EmptyRule() {
    }

    @Override
    public RuleResult<Node, ParseError> parse(String input) {
        return input.isEmpty()
                ? new RuleResult<>(new Ok<>(new Node()))
                : new RuleResult<>(Err.Err(new ParseError("Input not empty", input)));
    }

    @Override
    public RuleResult<String, GenerateError> generate(Node node) {
        return new RuleResult<>(new Ok<>(""));
    }
}
