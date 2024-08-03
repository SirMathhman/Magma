package magma.app.compile.rule;

import magma.app.compile.GenerateError;
import magma.app.compile.Node;
import magma.app.compile.ParseError;
import magma.api.Err;
import magma.api.Ok;
import magma.api.Result;

import java.util.Optional;

public class EmptyRule implements Rule {
    public static final Rule EMPTY_RULE = new EmptyRule();

    private EmptyRule() {
    }

    private Optional<Node> parse0(String input) {
        return input.isEmpty() ? Optional.of(new Node()) : Optional.empty();
    }


    private Result<Node, ParseError> parse1(String input) {
        return parse0(input)
                .<Result<Node, ParseError>>map(Ok::new)
                .orElseGet(() -> new Err<>(new ParseError("Invalid input", input)));
    }

    private Result<String, GenerateError> generate1(Node node) {
        return new Ok<>("");
    }

    @Override
    public RuleResult<Node, ParseError> parse(String input) {
        return new RuleResult<>(parse1(input));
    }

    @Override
    public RuleResult<String, GenerateError> generate(Node node) {
        return new RuleResult<>(generate1(node));
    }
}
