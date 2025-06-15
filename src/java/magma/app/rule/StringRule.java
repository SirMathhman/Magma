package magma.app.rule;

import magma.app.node.MapNode;
import magma.app.node.Node;
import magma.app.Rule;

import java.util.Optional;

public record StringRule(String key) implements Rule {
    @Override
    public Optional<Node> lex(String input) {
        Node node = new MapNode();
        return Optional.of(node.strings()
                .with(this.key, input));
    }

    @Override
    public Optional<String> generate(Node node) {
        return node.strings()
                .find(this.key);
    }
}