package magma;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DirectorySourceSet implements SourceSet {
    private final Path root;

    public DirectorySourceSet(Path root) {
        this.root = root;
    }

    private Set<Path> collect() throws IOException {
        try (var stream = Files.walk(root)) {
            return stream.collect(Collectors.toSet());
        }
    }

    @Override
    public Stream<Unit> stream() throws IOException {
        return collect().stream()
                .filter(Files::isRegularFile)
                .filter(path -> path.toString().endsWith(".java"))
                .map(readableChild -> new PathUnit(root, readableChild));
    }
}
