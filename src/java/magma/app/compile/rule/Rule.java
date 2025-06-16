package magma.app.compile.rule;

import magma.app.compile.error.node.NodeResult;
import magma.app.compile.error.string.StringResult;

public interface Rule<Node> {
    NodeResult<Node> lex(String input);

    StringResult generate(Node node);
}
