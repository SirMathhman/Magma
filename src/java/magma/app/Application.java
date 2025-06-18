package magma.app;

import magma.api.io.error.IOError;

import java.util.Optional;

public interface Application {
    Optional<IOError> run();
}
