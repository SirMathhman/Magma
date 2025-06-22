package magma.rule;

import magma.node.result.NodeResult;

public interface Rule<Node, StringResult> {
    NodeResult<Node> lex(String input);

    StringResult generate(Node node);
}
