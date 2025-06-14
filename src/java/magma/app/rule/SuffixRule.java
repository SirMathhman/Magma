package magma.app.rule;

import magma.app.node.Node;
import magma.app.rule.result.GenerationResult;
import magma.app.rule.result.LexResult;
import magma.app.rule.result.optional.OptionalLexResult;

public record SuffixRule(Rule rule, String suffix) implements Rule {
    @Override
    public LexResult lex(String input) {
        if (!input.endsWith(this.suffix))
            return OptionalLexResult.createEmpty();
        final var slice = input.substring(0, input.length() - this.suffix.length());
        return this.rule.lex(slice);
    }

    @Override
    public GenerationResult generate(Node node) {
        return this.rule.generate(node).map(value -> value + this.suffix);
    }
}