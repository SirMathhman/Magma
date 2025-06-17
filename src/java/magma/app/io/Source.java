package magma.app.io;

import magma.api.list.List;

public interface Source {
    String computeName();

    List<String> computeNamespace();
}
