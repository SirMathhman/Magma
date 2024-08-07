package magma.app;

import magma.api.Result;

import java.io.IOException;
import java.util.stream.Stream;

public interface Unit {
    Stream<String> computeNamespace();

    Result<String, IOException> read();

    String computeName();

    String format();
}
