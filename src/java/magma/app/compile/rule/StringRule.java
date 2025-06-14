package magma.app.compile.rule;

import magma.app.compile.Rule;
import magma.app.compile.node.CompoundNode;
import magma.app.compile.node.PropertiesCompoundNode;
import magma.app.compile.rule.result.LexResult;
import magma.app.compile.rule.result.optional.OptionalLexResult;

public final class StringRule implements Rule<CompoundNode> {
    private final String key;

    public StringRule(String key) {
        this.key = key;
    }

    @Override
    public LexResult<CompoundNode> lex(String input) {
        return OptionalLexResult.of(new PropertiesCompoundNode().strings().with(this.key, input));
    }

    @Override
    public LexResult<String> generate(CompoundNode node) {
        return new OptionalLexResult<>(node.strings().find(this.key));
    }
}