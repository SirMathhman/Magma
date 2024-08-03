package magma.app.compile.rule;

import magma.app.compile.GenerateError;
import magma.app.compile.Node;
import magma.app.compile.ParseError;
import magma.api.Err;
import magma.api.Ok;
import magma.api.Result;

import java.util.Optional;

public record NodeRule(String propertyKey, Rule child) implements Rule {
    private Optional<Node> parse0(String input) {
        return this.child().parse(input).result().findValue().map(node -> new Node().withNode(propertyKey(), node));
    }

    private Result<Node, ParseError> parse1(String input) {
        return parse0(input)
                .<Result<Node, ParseError>>map(Ok::new)
                .orElseGet(() -> new Err<>(new ParseError("Invalid input", input)));
    }

    private Result<String, GenerateError> generate1(Node node) {
        return node.findNode(propertyKey)
                .map(node1 -> child.generate(node1).result())
                .orElseGet(() -> new Err<>(new GenerateError("Node '" + propertyKey + "' not present", node)));
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