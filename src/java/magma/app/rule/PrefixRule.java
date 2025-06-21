package magma.app.rule;

import magma.api.optional.OptionalLike;
import magma.api.optional.Optionals;
import magma.app.node.Node;

public record PrefixRule(String prefix, Rule rule) implements Rule {
    @Override
    public OptionalLike<Node> lex(final String input) {
        if (!input.startsWith(this.prefix))
            return Optionals.empty();

        final var prefixLength = this.prefix.length();
        final var slice = input.substring(prefixLength);
        return this.rule.lex(slice);
    }

    @Override
    public OptionalLike<String> generate(final Node node) {
        return this.rule.generate(node)
                .map(result -> this.prefix + result);
    }
}