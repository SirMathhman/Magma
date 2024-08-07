package magma.app;

import java.nio.file.Path;
import java.util.Set;

public interface SourceSet {
    Set<Path> collectSources();
}
