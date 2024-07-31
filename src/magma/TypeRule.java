package magma;

import java.util.Optional;

public record TypeRule(String type, Rule child) implements Rule {
    @Override
    public Optional<Node> parse(String input) {
        return child.parse(input).map(node -> node.retype(type));
    }

    @Override
    public Optional<String> generate(Node node) {
        return node.is(type) ? child.generate(node) : Optional.empty();
    }
}
