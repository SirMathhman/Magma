package magma.app.compile;

import magma.api.optional.OptionalLike;

public interface Rule {
    OptionalLike<Node> lex(String input);

    OptionalLike<String> generate(Node node);
}
