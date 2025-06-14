package magma.app.rule;

import magma.app.node.CompoundNode;
import magma.app.rule.result.GenerationResult;
import magma.app.rule.result.LexResult;

public interface Rule {
    LexResult lex(String input);

    GenerationResult generate(CompoundNode node);
}
