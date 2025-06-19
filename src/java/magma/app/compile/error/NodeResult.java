package magma.app.compile.error;

import java.util.Optional;

public interface NodeResult<Node> {
    Optional<Node> findValue();
}
