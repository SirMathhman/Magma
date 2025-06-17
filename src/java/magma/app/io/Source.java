package magma.app.io;

import magma.api.list.Iterable;

public interface Source {
    String computeName();

    Iterable<String> computeNamespace();
}
