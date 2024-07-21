package magma;

import java.nio.file.Path;
import java.util.stream.Stream;

public interface SourceSet {
    Stream<Path> streamPaths();
}
