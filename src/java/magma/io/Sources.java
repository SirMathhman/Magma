package magma.io;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;
import java.util.stream.Collectors;

public record Sources(Path sourceDirectory) {
    public Set<Source> collect() throws IOException {
        try (final var stream = Files.walk(this.sourceDirectory)) {
            return stream.filter(Files::isRegularFile)
                    .filter(path -> path.toString()
                            .endsWith(".java"))
                    .map(source -> new PathSource(this.sourceDirectory, source))
                    .collect(Collectors.toSet());
        }
    }
}