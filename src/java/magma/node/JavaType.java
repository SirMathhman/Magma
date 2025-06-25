package magma.node;

import java.util.Optional;

public interface JavaType {
    CType toCType();

    Optional<String> findBaseName();
}
