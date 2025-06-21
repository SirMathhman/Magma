package magma.app;

import magma.app.node.Node;
import magma.app.optional.OptionalLike;

public interface Rule {
    OptionalLike<Node> lex(String input);

    OptionalLike<String> generate(Node node);
}
