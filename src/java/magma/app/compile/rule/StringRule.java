package magma.app.compile.rule;

import magma.app.compile.node.MapNode;
import magma.app.compile.node.Node;

import java.util.Optional;

public record StringRule(String key) implements Rule {
    @Override
    public Optional<String> generate(Node node) {
        return node.findString(this.key);
    }

    @Override
    public Optional<Node> lex(String input) {
        return Optional.of(new MapNode().withString(this.key, input));
    }
}