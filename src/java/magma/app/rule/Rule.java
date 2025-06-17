package magma.app.rule;

import magma.app.node.Node;

import java.util.Optional;

public interface Rule {
    Optional<Node> lex(String input);
}
