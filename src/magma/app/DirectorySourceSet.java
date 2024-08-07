package magma.app;

import magma.api.Err;
import magma.api.Ok;
import magma.api.Result;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;
import java.util.stream.Collectors;

public record DirectorySourceSet(Path root, String extension) implements SourceSet {
    private Set<Unit> collectSources1() throws IOException {
        try (var stream = Files.walk(root)) {
            var set = stream.collect(Collectors.toSet());
            return set.stream()
                    .filter(Files::isRegularFile)
                    .filter(path -> path.getFileName().toString().endsWith("." + extension))
                    .map(path -> new PathUnit(root, path))
                    .collect(Collectors.toSet());
        }
    }

    @Override
    public Result<Set<Unit>, IOException> collectSources() {
        try {
            return new Ok<>(collectSources1());
        } catch (IOException e) {
            return Err.Err(e);
        }
    }
}
