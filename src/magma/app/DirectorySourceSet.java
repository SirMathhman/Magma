package magma.app;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;
import java.util.stream.Collectors;

public record DirectorySourceSet(Path root, String extension) implements SourceSet {
    @Override
    public Set<Unit> collectSources() throws IOException {
        try (var stream = Files.walk(root)) {
            var set = stream.collect(Collectors.toSet());
            return set.stream()
                    .filter(Files::isRegularFile)
                    .filter(path -> path.getFileName().toString().endsWith("." + extension))
                    .map(path -> new PathUnit(root, path))
                    .collect(Collectors.toSet());
        }
    }
}
