package magma.app.io.source;

import magma.api.Result;
import magma.app.io.location.Location;

import java.io.IOException;

public interface Source {
    Result<String, IOException> readString();

    Location computeLocation();
}
