package magma.node.result;

import java.util.Optional;

public interface NodeResult<Node> {
    @Deprecated
    Optional<Node> toOptional();
}
