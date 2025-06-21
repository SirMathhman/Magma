package magma.app.compile.rule;

import magma.api.optional.OptionalLike;
import magma.app.compile.Node;

public interface Rule {
    OptionalLike<Node> lex(String input);

    OptionalLike<String> generate(Node node);
}
