package magma.app.compile.rule;

import magma.app.compile.node.NodeWithEverything;

import java.util.Optional;

public record SuffixRule(Rule rule, String suffix) implements Rule {
    @Override
    public Optional<NodeWithEverything> lex(String input) {
        if (!input.endsWith(this.suffix))
            return Optional.empty();

        final var withoutSuffix = input.substring(0, input.length() - this.suffix.length());
        return this.rule.lex(withoutSuffix);
    }

    @Override
    public Optional<String> generate(NodeWithEverything node) {
        return this.rule.generate(node)
                .map(result -> result + this.suffix);
    }
}