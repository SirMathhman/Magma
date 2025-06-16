package magma.app.compile.rule;

import magma.app.compile.error.NodeResult;
import magma.app.compile.error.StringResult;

public interface Rule<Node> {
    NodeResult<Node> lex(String input);

    StringResult generate(Node node);
}
