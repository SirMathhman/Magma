package magma.app;

import magma.api.io.IOError;
import magma.api.optional.OptionalLike;

public interface Application {
    OptionalLike<IOError> run();
}
