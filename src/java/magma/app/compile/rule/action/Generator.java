package magma.app.compile.rule.action;

import java.util.Optional;

public interface Generator<Node> {
    Optional<String> generate(Node node);
}
