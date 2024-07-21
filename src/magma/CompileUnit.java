package magma;

import java.util.stream.Stream;

public interface CompileUnit {
    String computeName();

    Stream<String> computeNamespace();
}
