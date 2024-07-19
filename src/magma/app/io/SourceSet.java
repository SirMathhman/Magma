package magma.app.io;

import java.io.IOException;
import java.util.stream.Stream;

public interface SourceSet {
    Stream<Unit> stream() throws IOException;
}
