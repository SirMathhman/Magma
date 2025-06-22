package magma.rule;

import magma.node.result.NodeResult;

public interface Rule {
    NodeResult lex(String input);
}
