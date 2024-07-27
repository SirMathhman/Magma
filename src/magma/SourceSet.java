package magma;

import java.io.IOException;
import java.util.stream.Stream;

public interface SourceSet {
    Stream<Source> walk() throws IOException;
}
