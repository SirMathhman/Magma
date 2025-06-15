package magma.app.compile.rule;

import magma.app.compile.node.NodeWithEverything;

import java.util.Optional;

public record PrefixRule(String prefix, Rule rule) implements Rule {
    @Override
    public Optional<NodeWithEverything> lex(String input) {
        if (!input.startsWith(this.prefix))
            return Optional.empty();

        final var withoutPrefix = input.substring(this.prefix.length());
        return this.rule.lex(withoutPrefix);
    }

    @Override
    public Optional<String> generate(NodeWithEverything node) {
        return this.rule.generate(node)
                .map(result -> this.prefix + result);
    }
}