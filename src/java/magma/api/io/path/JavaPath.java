package magma.api.io.path;

import magma.api.collect.stream.JavaStream;
import magma.api.collect.stream.StreamLike;
import magma.api.io.IOError;
import magma.api.io.JavaIOError;
import magma.api.optional.OptionalLike;
import magma.api.optional.Optionals;
import magma.api.result.Err;
import magma.api.result.Ok;
import magma.api.result.Result;

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
    public Result<StreamLike<PathLike>, IOError> walk() {
        try {
            return new Ok<>(new JavaStream<>(Files.walk(this.path)).map(JavaPath::new));
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

    @Override
    public PathLike getFileName() {
        return new JavaPath(this.path.getFileName());
    }

    @Override
    public String asString() {
        return this.path.toString();
    }

    @Override
    public boolean isRegularFile() {
        return Files.isRegularFile(this.path);
    }
}