package magma.app.rule;

import magma.app.node.MapNode;
import magma.app.node.Node;

import java.util.Optional;

public record StringRule(String key) implements Rule {
    @Override
    public Optional<Node> lex(String input) {
        return Optional.of(new MapNode().withString(this.key, input));
    }

    @Override
    public Optional<String> generate(Node node) {
        return node.findString(this.key);
    }
}