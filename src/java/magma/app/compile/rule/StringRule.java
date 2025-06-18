package magma.app.compile.rule;

import magma.app.compile.node.MapNodeWithEverything;
import magma.app.compile.node.NodeWithEverything;

import java.util.Optional;

public record StringRule(String key) implements Rule {
    @Override
    public Optional<String> generate(NodeWithEverything node) {
        return node.findString(this.key);
    }

    @Override
    public Optional<NodeWithEverything> lex(String input) {
        return Optional.of(new MapNodeWithEverything().withString(this.key, input));
    }
}