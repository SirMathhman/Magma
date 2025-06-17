package magma.api.io;

import java.util.function.Consumer;

public interface IOOption {
    void ifPresent(Consumer<IOError> consumer);
}
