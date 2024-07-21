package magma;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

public record SingleSourceSet(Path source) implements SourceSet {
    @Override
    public Stream<CompileUnit> streamPaths() {
        return Files.exists(this.source())
                ? Stream.of(new PathUnit(source.getParent(), source))
                : Stream.empty();
    }
}