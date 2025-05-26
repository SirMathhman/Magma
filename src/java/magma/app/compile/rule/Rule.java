package magma.app.compile.rule;

import magma.api.option.Option;
import magma.app.compile.node.Node;

public interface Rule {
    Option<Node> lex(String input);
}
