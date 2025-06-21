package magma.rule;

import magma.MapNode;
import magma.Node;
import magma.OptionalLike;
import magma.Optionals;

public record StringRule(String key) implements Rule {
    @Override
    public OptionalLike<Node> lex(final String input) {
        final var node = MapNode.empty()
                .withString(this.key, input);

        return Optionals.of(node);
    }

    @Override
    public OptionalLike<String> generate(final Node node) {
        return node.findString(this.key);
    }
}