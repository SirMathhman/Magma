package magma.rule;

import magma.Node;
import magma.optional.OptionalLike;

public interface Rule {
    OptionalLike<Node> lex(String input);

    OptionalLike<String> generate(Node node);
}
