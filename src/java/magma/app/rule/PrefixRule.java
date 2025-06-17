package magma.app.rule;

import magma.app.node.Node;

import java.util.Optional;

public record PrefixRule(String prefix, Rule rule) implements Rule {
    @Override
    public Optional<Node> lex(String input) {
        if (!input.startsWith(this.prefix()))
            return Optional.empty();

        final var slice = input.substring(this.prefix()
                .length());
        return this.rule()
                .lex(slice);
    }

    @Override
    public Optional<String> generate(Node node) {
        return this.rule.generate(node)
                .map(result -> this.prefix + result);
    }
}