package magma;

import java.util.Optional;

public interface Rule {
    Optional<Node> parse(String input);
}
