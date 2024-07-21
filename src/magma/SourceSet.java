package magma;

import java.io.IOException;
import java.util.stream.Stream;

public interface SourceSet {
    Stream<CompileUnit> streamPaths() throws IOException;
}
