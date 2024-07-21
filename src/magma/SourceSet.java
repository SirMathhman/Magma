package magma;

import java.io.IOException;
import java.nio.file.Path;
import java.util.stream.Stream;

public interface SourceSet {
    Stream<Path> streamPaths() throws IOException;
}
