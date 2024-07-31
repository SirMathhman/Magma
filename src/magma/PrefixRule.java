package magma;

import java.util.Optional;

public record PrefixRule(String prefix, Rule child) implements Rule {
    private Optional<Node> parse0(String input) {
        if (!input.startsWith(prefix())) return Optional.empty();
        var truncatedRight = input.substring(prefix().length());
        return this.child().parse(truncatedRight).findValue();
    }

    private Optional<String> generate0(Node node) {
        return child.generate(node).findValue().map(inner -> prefix + inner);
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