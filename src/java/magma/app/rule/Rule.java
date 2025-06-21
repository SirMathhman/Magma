package magma.app.rule;

import magma.api.optional.OptionalLike;
import magma.app.node.Node;

public interface Rule {
    OptionalLike<Node> lex(String input);

    OptionalLike<String> generate(Node node);
}
