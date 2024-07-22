package magma.app.io;

import java.io.IOException;
import java.util.stream.Stream;

public interface Unit {
    String computeName();

    Stream<String> computeNamespace();

    String read() throws IOException;
}
