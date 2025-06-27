package magma;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class JavaFiles {
    private JavaFiles() {
    }

    @Actual
    static Result<List<Path>, IOException> walk(final Path root) {
        try (final var stream = Files.walk(root)) {
            return new Ok<>(stream.toList());
        } catch (final IOException e) {
            return new Err<>(e);
        }
    }
}
