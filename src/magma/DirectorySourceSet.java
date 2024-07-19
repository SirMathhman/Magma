package magma;

import java.nio.file.Path;
import java.util.stream.Stream;

public class DirectorySourceSet implements SourceSet {
    @Override
    public Stream<Path> stream() {
        throw new UnsupportedOperationException();
    }
}
