package magma.app.compile.rule;

import magma.app.compile.GenerateError;
import magma.app.compile.Node;
import magma.app.compile.ParseError;
import magma.api.Err;
import magma.api.Ok;
import magma.api.Result;

import java.util.Optional;

public record StripRule(Rule child) implements Rule {
    private Optional<Node> parse0(String input) {
        return child.parse(input.strip()).result().findValue();
    }

    private Optional<String> generate0(Node node) {
        throw new UnsupportedOperationException();
    }

    private Result<Node, ParseError> parse1(String input) {
        return parse0(input)
                .<Result<Node, ParseError>>map(Ok::new)
                .orElseGet(() -> new Err<>(new ParseError("Invalid input", input)));
    }

    private Result<String, GenerateError> generate1(Node node) {
        return generate0(node)
                .<Result<String, GenerateError>>map(Ok::new)
                .orElseGet(() -> new Err<>(new GenerateError("Invalid node", node)));
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
