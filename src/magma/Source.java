package magma;

import java.util.stream.Stream;

public interface Source {
    Stream<String> computeNamespace();

    String computeName();
}
