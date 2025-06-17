package magma.io;

import java.io.IOException;

public interface Source {
    String readString() throws IOException;

    Location computeLocation();
}
