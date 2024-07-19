package magma;

import java.util.stream.Stream;

public interface SourceSet {
    Stream<PathSource> stream();
}
