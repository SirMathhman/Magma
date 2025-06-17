package magma.app.io;

import magma.api.list.Streamable;

public interface Source {
    String computeName();

    Streamable<String> computeNamespace();
}
