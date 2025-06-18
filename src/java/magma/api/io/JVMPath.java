package magma.api.io;

import magma.api.Err;
import magma.api.Ok;
import magma.api.Result;
import magma.app.PathLike;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public record JVMPath(Path path) implements PathLike {
    @Override
    public String getFileNameAsString() {
        return new JVMPath(this.path.getFileName()).asString();
    }

    @Override
    public Result<String, IOError> readString() {
        try {
            return new Ok<>(Files.readString(this.path));
        } catch (IOException e) {
            return new Err<>(new ThrowableIOError(e));
        }
    }

    @Override
    public Optional<IOError> writeString(CharSequence content) {
        try {
            Files.writeString(this.path, content);
            return Optional.empty();
        } catch (IOException e) {
            return Optional.of(new ThrowableIOError(e));
        }
    }

    @Override
    public boolean isRegularFile() {
        return Files.isRegularFile(this.path);
    }

    @Override
    public Result<Set<PathLike>, IOError> walk() {
        try (var stream = Files.walk(this.path)) {
            return new Ok<>(stream.map(JVMPath::new)
                    .collect(Collectors.toSet()));
        } catch (IOException e) {
            return new Err<>(new ThrowableIOError(e));
        }
    }

    @Override
    public String asString() {
        return this.path.toString();
    }
}
