package magma;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

public record SingleSourceSet(Path source) implements SourceSet {
    private Stream<Path> walk0() {
        return Files.exists(source)
                ? Stream.of(source)
                : Stream.empty();
    }

    @Override
    public Stream<Source> walk() throws IOException {
        return walk0().map(PathSource::new);
    }
}