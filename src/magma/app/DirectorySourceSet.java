package magma.app;

import magma.api.Err;
import magma.api.Ok;
import magma.api.Result;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class DirectorySourceSet implements SourceSet {
    private final Path root;
    private final String extension;

    public DirectorySourceSet(Path root, String extension) {
        this.root = root;
        this.extension = extension;
    }

    @Override
    public Result<Set<Unit>, IOException> collectSources() {
        try {
            // TODO: try with resources
            var stream = Files.walk(root);
            var collect = filterSources(stream);
            return new Ok<>(collect);
        } catch (IOException e) {
            return Err.Err(e);
        }
    }

    private Set<Unit> filterSources(Stream<Path> stream) {
        return stream
                .filter(Files::isRegularFile)
                .filter(path -> path.getFileName().toString().endsWith("." + extension))
                .map(path -> new PathUnit(root, path))
                .collect(Collectors.toSet());
    }
}
