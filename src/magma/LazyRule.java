package magma;

import java.util.Optional;

public class LazyRule implements Rule {
    private Optional<Rule> current = Optional.empty();

    public void set(Rule rule) {
        this.current = Optional.of(rule);
    }

    private Optional<Node> parse0(String input) {
        return current.flatMap(inner -> inner.parse(input).findValue());
    }

    private Optional<String> generate0(Node node) {
        return current.flatMap(inner -> inner.generate(node).findValue());
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
