package magma.rule;

import magma.node.result.NodeResult;
import magma.string.result.StringResult;

public interface Rule<Node> {
    NodeResult<Node> lex(String input);

    StringResult generate(Node node);
}