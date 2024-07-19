package magma;

import java.io.IOException;
import java.util.stream.Stream;

public interface Source {
    Stream<String> computeNamespace();

    String computeName();

    String read() throws IOException;
}
