package magma.rule;

import java.util.Optional;

public interface Rule<Node> {
    NodeResult<Node> lex(String input);

    Optional<String> generate(Node node);
}
