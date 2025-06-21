package magma.app.compile.rule;

import magma.api.optional.OptionalLike;
import magma.api.optional.Optionals;
import magma.app.compile.MapNode;
import magma.app.compile.Node;

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