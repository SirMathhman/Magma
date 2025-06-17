package magma.api.list;

import java.util.Optional;

public interface Head<T> {
    Optional<T> next();
}
