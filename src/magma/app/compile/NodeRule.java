package magma.app.compile;

import java.util.Optional;

public record NodeRule(String propertyKey, Rule child) implements Rule {
    @Override
    public Optional<Node> parse(String input) {
        return child.parse(input).map(node -> new Node().withNode(propertyKey, node));
    }

    @Override
    public Optional<String> generate(Node node) {
        return node.findNode(propertyKey).flatMap(child::generate);
    }
}