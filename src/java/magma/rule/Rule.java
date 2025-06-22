package magma.rule;

import magma.node.Node;
import magma.option.Option;

interface Rule {
    Option<Node> lex(String input);
}
