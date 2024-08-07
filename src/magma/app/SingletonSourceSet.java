package magma.app;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Set;

public record SingletonSourceSet(Path source) implements SourceSet {
    @Override
    public Set<Path> collectSources() {
        return Files.exists(source())
                ? Collections.singleton(source())
                : Collections.<Path>emptySet();
    }
}