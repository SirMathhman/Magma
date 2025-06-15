package magma.app.compile.rule;

import magma.app.compile.node.MapNode;
import magma.app.compile.Node;
import magma.app.compile.Rule;

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