package magma;

import java.util.Optional;

public record StripRule(Rule child) implements Rule {
    private Optional<Node> parse0(String input) {
        return child.parse(input.strip()).findValue();
    }

    private Optional<String> generate0(Node node) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Result<Node, ParseException> parse(String input) {
        return parse0(input)
                .<Result<Node, ParseException>>map(Ok::new)
                .orElseGet(() -> new Err<>(new ParseException("Invalid input", input)));
    }

    @Override
    public Result<String, GenerateException> generate(Node node) {
        return generate0(node)
                .<Result<String, GenerateException>>map(Ok::new)
                .orElseGet(() -> new Err<>(new GenerateException("Invalid node", node)));
    }
}
