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

    @Actual
    static Optional<IOException> writeString(final Path path, final CharSequence output) {
        try {
            Files.writeString(path, output);
            return new None<>();
        } catch (final IOException e) {
            return new Some<>(e);
        }
    }

    @Actual
    static Optional<IOException> createDirectories(final Path path) {
        try {
            Files.createDirectories(path);
            return new None<>();
        } catch (final IOException e) {
            return new Some<>(e);
        }
    }

    @Actual
    static Result<String, IOException> readString(final Path source) {
        try {
            return new Ok<>(Files.readString(source));
        } catch (final IOException e) {
            return new Err<>(e);
        }
    }
}
