package magma.app.compile.rule;

import magma.app.compile.node.NodeWithEverything;

import java.util.Optional;

public interface Rule {
    Optional<String> generate(NodeWithEverything node);

    Optional<NodeWithEverything> lex(String input);
}
