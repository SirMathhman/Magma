package magma.app.rule;

import magma.app.node.Node;
import magma.app.rule.result.GenerationResult;
import magma.app.rule.result.LexResult;

public record PrefixRule(String prefix, Rule rule) implements Rule {
    @Override
    public LexResult lex(String input) {
        if (!input.startsWith(this.prefix)) return LexResult.createEmpty();
        final var slice = input.substring(this.prefix.length());
        return this.rule.lex(slice);
    }

    @Override
    public GenerationResult generate(Node node) {
        return this.rule.generate(node).map(value -> this.prefix + value);
    }
}