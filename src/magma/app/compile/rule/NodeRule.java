package magma.app.compile.rule;

import magma.app.compile.GenerateException;
import magma.app.compile.Node;
import magma.app.compile.ParseException;
import magma.api.Err;
import magma.api.Ok;
import magma.api.Result;

import java.util.Optional;

public record NodeRule(String propertyKey, Rule child) implements Rule {
    private Optional<Node> parse0(String input) {
        return this.child().parse(input).result().findValue().map(node -> new Node().withNode(propertyKey(), node));
    }

    private Result<Node, ParseException> parse1(String input) {
        return parse0(input)
                .<Result<Node, ParseException>>map(Ok::new)
                .orElseGet(() -> new Err<>(new ParseException("Invalid input", input)));
    }

    private Result<String, GenerateException> generate1(Node node) {
        return node.findNode(propertyKey)
                .map(node1 -> child.generate(node1).result())
                .orElseGet(() -> new Err<>(new GenerateException("Node '" + propertyKey + "' not present", node)));
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