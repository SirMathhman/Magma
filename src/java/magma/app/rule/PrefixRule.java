package magma.app.rule;

import magma.app.node.CompoundNode;
import magma.app.rule.result.GenerationResult;
import magma.app.rule.result.LexResult;
import magma.app.rule.result.optional.OptionalLexResult;

public record PrefixRule(String prefix, Rule<CompoundNode> rule) implements Rule<CompoundNode> {
    @Override
    public LexResult lex(String input) {
        if (!input.startsWith(this.prefix)) return OptionalLexResult.createEmpty();
        final var slice = input.substring(this.prefix.length());
        return this.rule.lex(slice);
    }

    @Override
    public GenerationResult generate(CompoundNode node) {
        return this.rule.generate(node).map(value -> this.prefix + value);
    }
}