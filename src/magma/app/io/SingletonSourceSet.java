package magma.app.io;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.stream.Stream;

public record SingletonSourceSet(Path source) implements SourceSet {
    private Stream<Path> stream0() {
        var sources = new HashSet<Path>();
        if (Files.exists(source())) {
            sources.add(source());
        }
        return sources.stream();
    }

    @Override
    public Stream<Unit> stream() {
        return stream0().map(readableChild -> new PathUnit(Paths.get("."), readableChild));
    }
}