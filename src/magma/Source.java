package magma;

import java.util.stream.Stream;

public interface Source {
    String computeName();

    Stream<String> computeNamespace();
}
