package magma.app.io;

import java.util.Collection;

public interface Source {
    String computeName();

    Collection<String> computeNamespace();
}
