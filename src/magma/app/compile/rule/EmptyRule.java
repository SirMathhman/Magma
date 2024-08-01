package magma.app.compile.rule;

import magma.app.compile.GenerateException;
import magma.app.compile.Node;
import magma.app.compile.ParseException;
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


    private Result<Node, ParseException> parse1(String input) {
        return parse0(input)
                .<Result<Node, ParseException>>map(Ok::new)
                .orElseGet(() -> new Err<>(new ParseException("Invalid input", input)));
    }

    private Result<String, GenerateException> generate1(Node node) {
        return new Ok<>("");
    }

    @Override
    public RuleResult<Node, ParseException> parse(String input) {
        return new RuleResult<>(parse1(input));
    }

    @Override
    public RuleResult<String, GenerateException> generate(Node node) {
        return new RuleResult<>(generate1(node));
    }
}
