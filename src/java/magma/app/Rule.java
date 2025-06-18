package magma.app;

import magma.app.node.Node;

import java.util.Optional;

public interface Rule {
    Optional<String> generate(Node node);

    Optional<Node> lex(String input);
}
