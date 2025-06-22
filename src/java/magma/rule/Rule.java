package magma.rule;

import magma.node.Node;
import magma.node.result.NodeResult;
import magma.option.Option;

public interface Rule {
    NodeResult lex(String input);

    Option<String> generate(Node node);
}
