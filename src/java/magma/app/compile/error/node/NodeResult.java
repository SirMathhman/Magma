package magma.app.compile.error.node;

import java.util.Optional;

public interface NodeResult<Node> {
    Optional<Node> findValue();
}
