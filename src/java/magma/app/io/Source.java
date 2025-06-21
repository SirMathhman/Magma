package magma.app.io;

import magma.api.io.IOError;
import magma.api.result.Result;

public interface Source {
    Result<String, IOError> read();

    String computeName();
}
