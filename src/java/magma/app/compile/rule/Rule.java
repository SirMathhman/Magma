package magma.app.compile.rule;

import magma.app.compile.node.Node;
import magma.app.compile.result.GenerateResult;
import magma.app.compile.result.LexResult;

public interface Rule {
    LexResult lex(String input);

    GenerateResult generate(Node node);
}