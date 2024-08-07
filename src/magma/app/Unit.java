package magma.app;

import java.io.IOException;
import java.util.stream.Stream;

public interface Unit {
    Stream<String> computeNamespace();

    String read() throws IOException;

    String computeName();
}
