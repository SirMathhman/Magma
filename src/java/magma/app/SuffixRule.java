package magma.app;

import magma.OptionalLike;
import magma.app.node.Node;

public record SuffixRule(Rule child, String suffix) implements Rule {
    @Override
    public OptionalLike<Node> lex(final String input) {
        if (!input.endsWith(this.suffix))
            return OptionalLike.empty();

        final var inputLength = input.length();
        final var suffixLength = this.suffix.length();
        final var slice = input.substring(0, inputLength - suffixLength);
        return this.child.lex(slice);
    }

    @Override
    public OptionalLike<String> generate(final Node node) {
        return this.child.generate(node)
                .map(result -> result + this.suffix);
    }
}