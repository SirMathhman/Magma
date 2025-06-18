package magma.app;

import magma.api.io.IOError;

import java.util.Optional;

public interface Application {
    Optional<IOError> run();
}
