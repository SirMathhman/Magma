package magma.app.rule;

import magma.app.node.Node;
import magma.app.rule.result.GenerationResult;
import magma.app.rule.result.LexResult;

public record StripRule(Rule rule) implements Rule {
    @Override
    public LexResult lex(String input) {
        return this.rule.lex(input.strip());
    }

    @Override
    public GenerationResult generate(Node node) {
        return this.rule.generate(node);
    }
}
