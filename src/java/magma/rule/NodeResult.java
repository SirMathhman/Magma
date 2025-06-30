package magma.rule;

import java.util.Optional;

public interface NodeResult<Node> {
    @Deprecated
    Optional<Node> toOptional();
}
