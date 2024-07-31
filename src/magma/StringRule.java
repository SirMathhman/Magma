package magma;

import java.util.Optional;

public record StringRule(String propertyKey) implements Rule {
    private Optional<Node> parse0(String input) {
        return Optional.of(new Node().withString(propertyKey(), input));
    }

    private Optional<String> generate0(Node node) {
        return node.findString(propertyKey);
    }

    @Override
    public Result<Node, ParseException> parse(String input) {
        return parse0(input)
                .<Result<Node, ParseException>>map(Ok::new)
                .orElseGet(() -> new Err<>(new ParseException("Invalid input", input)));
    }

    @Override
    public Result<String, GeneratingException> generate(Node node) {
        return generate0(node)
                .<Result<String, GeneratingException>>map(Ok::new)
                .orElseGet(() -> new Err<>(new GeneratingException("Invalid node", node)));
    }
}