package magma;

import java.util.Optional;

public record NodeRule(Rule child, String propertyKey) implements Rule {
    @Override
    public Optional<Node> parse(String input) {
        return child().parse(input).map(node -> new Node().withNode(propertyKey(), node));
    }

    @Override
    public Optional<String> generate(Node node) {
        throw new UnsupportedOperationException();
    }
}