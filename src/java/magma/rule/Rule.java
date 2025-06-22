package magma.rule;

import magma.node.result.NodeResult;

public interface Rule<Node, StringResult> {
    NodeResult lex(String input);

    StringResult generate(Node node);
}
