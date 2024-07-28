package magma.app.compile;

import java.util.Optional;

public interface Rule {
    Optional<Node> parse(String input);

    Optional<String> generate(Node node);
}
