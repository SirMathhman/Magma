package magma;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

public record SingleSourceSet(Path source) implements SourceSet {
    @Override
    public Stream<Path> streamPaths() {
        return Files.exists(source())
                ? Stream.of(source())
                : Stream.empty();
    }
}