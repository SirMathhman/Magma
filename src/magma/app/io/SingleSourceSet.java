package magma.app.io;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

public record SingleSourceSet(Path source) implements SourceSet {
    @Override
    public Stream<Source> walk() {
        var parent = source.getParent();
        if (parent == null) return Stream.empty();

        return Files.exists(source)
                ? Stream.of(new PathSource(parent, source))
                : Stream.empty();
    }
}