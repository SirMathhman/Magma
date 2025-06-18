package magma.app.compile.rule;

import java.util.Optional;

public record PrefixRule<Node>(String prefix, Rule<Node> rule) implements Rule<Node> {
    @Override
    public Optional<String> generate(Node node) {
        return this.rule.generate(node)
                .map(result -> this.prefix + result);
    }

    @Override
    public Optional<Node> lex(String input) {
        if (!input.startsWith(this.prefix))
            return Optional.empty();

        final var slice = input.substring(this.prefix.length());
        return this.rule.lex(slice);
    }
}