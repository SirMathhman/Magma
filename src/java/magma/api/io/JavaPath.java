package magma.api.io;

import magma.api.result.Err;
import magma.api.result.Ok;
import magma.api.result.Result;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public record JavaPath(Path path) implements PathLike {
    @Override
    public Optional<IOException> writeString(CharSequence output) {
        try {
            Files.writeString(this.path, output);
            return Optional.empty();
        } catch (IOException e) {
            return Optional.of(e);
        }
    }

    @Override
    public Result<Set<PathLike>, IOException> walk() {
        try (final var stream = Files.walk(this.path)) {
            return new Ok<>(stream.map(JavaPath::new)
                    .collect(Collectors.toSet()));
        } catch (IOException e) {
            return new Err<>(e);
        }
    }

    @Override
    public Result<String, IOException> readString() {
        try {
            return new Ok<>(Files.readString(this.path));
        } catch (IOException e) {
            return new Err<>(e);
        }
    }

    @Override
    public int getNameCount() {
        return this.path.getNameCount();
    }

    @Override
    public PathLike getName(int index) {
        return new JavaPath(this.path.getName(index));
    }

    @Override
    public String asString() {
        return this.path.toString();
    }

    @Override
    public PathLike relativize(PathLike child) {
        if (child instanceof JavaPath(var inner))
            return new JavaPath(this.path.relativize(inner));

        return this;
    }

    @Override
    public PathLike getParent() {
        return new JavaPath(this.path.getParent());
    }

    @Override
    public PathLike getFileName() {
        return new JavaPath(this.path.getFileName());
    }

    @Override
    public boolean isRegularFile() {
        return Files.isRegularFile(this.path);
    }
}
