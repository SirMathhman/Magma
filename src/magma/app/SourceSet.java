package magma.app;

import java.io.IOException;
import java.util.Set;

public interface SourceSet {
    Set<Unit> collectSources() throws IOException;
}
