package magma.rule;

import magma.node.Node;
import magma.node.result.NodeResult;
import magma.string.StringResult;

public interface Rule {
    NodeResult lex(String input);

    StringResult generate(Node node);
}
