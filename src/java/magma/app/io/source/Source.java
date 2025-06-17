package magma.app.io.source;

import magma.app.io.location.Location;

import java.io.IOException;

public interface Source {
    String readString() throws IOException;

    Location computeLocation();
}
