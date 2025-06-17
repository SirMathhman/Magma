package magma.app.io.source;

import magma.api.io.IOError;
import magma.api.result.Result;
import magma.app.io.location.Location;

public interface Source {
    Result<String, IOError> readString();

    Location computeLocation();
}
