package magma;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

public class DirectorySourceSet implements SourceSet {
    private Stream<Path> stream0() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Stream<Unit> stream() {
        return stream0().map(readableChild -> new PathUnit(Paths.get("."), readableChild));
    }
}
