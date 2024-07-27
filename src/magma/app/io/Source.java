package magma.app.io;

import java.util.stream.Stream;

public interface Source {
    String computeName();

    Stream<String> streamNamespace();
}
