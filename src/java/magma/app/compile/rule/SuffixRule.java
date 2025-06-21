package magma.app.compile.rule;

import magma.api.optional.OptionalLike;
import magma.api.optional.Optionals;
import magma.app.compile.Node;

public record SuffixRule(Rule child, String suffix) implements Rule {
    @Override
    public OptionalLike<Node> lex(final String input) {
        if (!input.endsWith(this.suffix))
            return Optionals.empty();

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