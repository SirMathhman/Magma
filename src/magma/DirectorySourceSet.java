package magma;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public record DirectorySourceSet(Path root) implements SourceSet {
    @Override
    public Stream<Source> walk() throws IOException {
        try (var stream = Files.walk(root)) {
            var set = stream.collect(Collectors.toSet());
            return set.stream()
                    .filter(Files::isRegularFile)
                    .map(PathSource::new);
        }
    }
}
