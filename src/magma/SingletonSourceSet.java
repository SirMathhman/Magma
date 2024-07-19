package magma;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.stream.Stream;

public record SingletonSourceSet(Path source) implements SourceSet {
    @Override
    public Stream<Path> stream() {
        var sources = new HashSet<Path>();
        if (Files.exists(source())) {
            sources.add(source());
        }
        return sources.stream();
    }
}