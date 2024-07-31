package magma;

import java.util.Optional;

public class LazyRule implements Rule {
    private Optional<Rule> current = Optional.empty();

    public void set(Rule rule) {
        this.current = Optional.of(rule);
    }

    @Override
    public Optional<Node> parse(String input) {
        return current.flatMap(inner -> inner.parse(input));
    }

    @Override
    public Optional<String> generate(Node node) {
        return current.flatMap(inner -> inner.generate(node));
    }
}
