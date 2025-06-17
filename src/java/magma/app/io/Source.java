package magma.app.io;

import magma.api.List;

public interface Source {
    String computeName();

    List<String> computeNamespace();
}
