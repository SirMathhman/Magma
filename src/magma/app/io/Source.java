package magma.app.io;

import java.io.IOException;
import java.util.stream.Stream;

public interface Source {
    String computeName();

    Stream<String> streamNamespace();

    String read() throws IOException;
}
