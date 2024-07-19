package magma.app.io;

import java.io.IOException;
import java.util.stream.Stream;

public interface Unit {
    Stream<String> computeNamespace();

    String computeName();

    String read() throws IOException;
}
