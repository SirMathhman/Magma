package magma.app.compile;

import magma.app.compile.node.Node;

import java.util.Optional;

public record SuffixRule(Rule rule, String suffix) implements Rule {
    @Override
    public Optional<String> generate(Node node) {
        return this.rule.generate(node)
                .map(result -> result + this.suffix);
    }

    @Override
    public Optional<Node> lex(String input) {
        if (!input.endsWith(this.suffix))
            return Optional.empty();

        final var slice = input.substring(0, input.length() - this.suffix.length());
        return this.rule.lex(slice);
    }
}