package magma;

import magma.api.Err;
import magma.api.Ok;
import magma.api.Result;

import java.util.Optional;

public record NodeRule(String propertyKey, Rule child) implements Rule {
    private Optional<Node> parse0(String input) {
        return this.child().parse(input).findValue().map(node -> new Node().withNode(propertyKey(), node));
    }

    @Override
    public Result<Node, ParseException> parse(String input) {
        return parse0(input)
                .<Result<Node, ParseException>>map(Ok::new)
                .orElseGet(() -> new Err<>(new ParseException("Invalid input", input)));
    }

    @Override
    public Result<String, GenerateException> generate(Node node) {
        return node.findNode(propertyKey)
                .map(child::generate)
                .orElseGet(() -> new Err<>(new GenerateException("Node '" + propertyKey + "' not present", node)));
    }
}