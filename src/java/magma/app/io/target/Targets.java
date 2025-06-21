package magma.app.io.target;

import magma.api.io.IOError;
import magma.api.optional.OptionalLike;

public interface Targets {
    OptionalLike<IOError> write(String output);
}
