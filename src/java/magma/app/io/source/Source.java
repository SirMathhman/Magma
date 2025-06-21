package magma.app.io.source;

import magma.api.io.IOError;
import magma.api.result.Result;

interface Source {
    Result<String, IOError> read();

    String computeName();
}
