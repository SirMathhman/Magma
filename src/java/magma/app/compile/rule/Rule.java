package magma.app.compile.rule;

import java.util.Optional;

public interface Rule<Node> {
    Optional<String> generate(Node node);

    Optional<Node> lex(String input);
}
