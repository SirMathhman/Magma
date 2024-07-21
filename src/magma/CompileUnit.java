package magma;

import java.io.IOException;
import java.util.stream.Stream;

public interface CompileUnit {
    String computeName();

    Stream<String> computeNamespace();

    String read() throws IOException;
}
