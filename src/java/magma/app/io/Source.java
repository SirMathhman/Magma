package magma.app.io;

import magma.api.collect.iter.Iterable;

public interface Source {
    String computeName();

    Iterable<String> computeNamespace();
}
