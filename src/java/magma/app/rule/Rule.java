package magma.app.rule;

import magma.app.Node;

import java.util.Optional;

public interface Rule {
    Optional<Node> lex(String input);
}
