package magma.app.compile.rule;

import magma.app.compile.result.NodeResult;
import magma.app.compile.result.StringResult;

public interface Rule<Node> {
    NodeResult lex(String input);

    StringResult generate(Node node);
}