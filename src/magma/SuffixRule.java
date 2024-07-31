package magma;

import java.util.Optional;

public record SuffixRule(Rule child, String suffix) implements Rule {
    private Optional<Node> parse0(String input) {
        if (!input.endsWith(suffix())) return Optional.empty();
        var name = input.substring(0, input.length() - suffix().length());
        return this.child().parse(name).findValue();
    }

    private Optional<String> generate0(Node node) {
        return child.generate(node).findValue().map(inner -> inner + suffix);
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