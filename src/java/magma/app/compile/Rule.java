package magma.app.compile;

import magma.app.compile.node.Node;

import java.util.Optional;

public interface Rule {
    Optional<String> generate(Node node);

    Optional<Node> lex(String input);
}
