package magma;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public record JavaPath(Path path) implements PathLike {
    @Override
    public Result<String, IOError> readString() {
        try {
            final var input = Files.readString(this.path);
            return new Ok<>(input);
        } catch (final IOException e) {
            return new Err<>(new JavaIOError(e));
        }
    }

    @Override
    public Result<StreamLike<Path>, IOError> walk() {
        try {
            return new Ok<>(new JavaStream<>(Files.walk(this.path)));
        } catch (final IOException e) {
            return new Err<>(new JavaIOError(e));
        }
    }

    @Override
    public OptionalLike<IOError> writeString(final String output) {
        try {
            Files.writeString(this.path, output);
            return Optionals.empty();
        } catch (final IOException e) {
            return Optionals.of(new JavaIOError(e));
        }
    }
}