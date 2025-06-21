package magma.rule;

import magma.Node;
import magma.OptionalLike;

public interface Rule {
    OptionalLike<Node> lex(String input);

    OptionalLike<String> generate(Node node);
}
