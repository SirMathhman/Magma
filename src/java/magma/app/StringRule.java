package magma.app;

import magma.OptionalLike;
import magma.app.node.MapNode;
import magma.app.node.Node;

public record StringRule(String key) implements Rule {
    @Override
    public OptionalLike<Node> lex(final String input) {
        final var node = MapNode.empty()
                .withString(this.key, input);

        return OptionalLike.of(node);
    }

    @Override
    public OptionalLike<String> generate(final Node node) {
        return node.findString(this.key);
    }
}