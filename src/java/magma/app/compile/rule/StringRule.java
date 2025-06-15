package magma.app.compile.rule;

import magma.app.compile.node.MapNode;
import magma.app.compile.node.NodeWithEverything;

import java.util.Optional;

public record StringRule(String key) implements Rule<NodeWithEverything> {
    @Override
    public Optional<NodeWithEverything> lex(String input) {
        NodeWithEverything node = new MapNode();
        return Optional.of(node.strings()
                .with(this.key, input));
    }

    @Override
    public Optional<String> generate(NodeWithEverything node) {
        return node.strings()
                .find(this.key);
    }
}