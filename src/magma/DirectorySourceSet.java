package magma;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DirectorySourceSet implements SourceSet {
    private final Path root;

    public DirectorySourceSet(Path root) {
        this.root = root;
    }

    @Override
    public Stream<CompileUnit> streamPaths() throws IOException {
        try (var resourceStream = Files.walk(root)) {
            return resourceStream
                    .filter(Files::isRegularFile)
                    .<CompileUnit>map(source -> new PathUnit(root, source))
                    .collect(Collectors.toSet())
                    .stream();
        }
    }
}
