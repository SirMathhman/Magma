package magma.app.compile.rule;

import magma.app.compile.result.GenerateResult;
import magma.app.compile.result.LexResult;

public interface Rule<Node> {
    LexResult lex(String input);

    GenerateResult generate(Node node);
}