package magma;

import magma.option.None;
import magma.option.Option;
import magma.option.Some;
import magma.result.Err;
import magma.result.Ok;
import magma.result.Result;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Wrapper implementation backed by {@code java.nio.file.Path}.
 */
public record JVMPath(Path path) implements PathLike {
    /**
     * Creates a new wrapper from a string path.
     */
    public static PathLike of(String first, String... more) {
        return new JVMPath(Path.of(first, more));
    }

    @Override
    public String toString() {
        return path.toString();
    }

    @Override
    public PathLike resolve(String other) {
        return new JVMPath(path.resolve(other));
    }

    @Override
    public PathLike resolve(PathLike other) {
        return new JVMPath(path.resolve(toPath(other)));
    }


    private Path toPath(PathLike other) {
        final var names = other.streamNames().toList();
        if (names.isEmpty()) {
            return Paths.get("");
        }

        final var first = Paths.get(names.getFirst());
        return names.subList(1, names.size())
                .stream()
                .reduce(first, Path::resolve, (_, next) -> next);
    }

    @Override
    public PathLike getParent() {
        Path parent = path.getParent();
        return new JVMPath(parent);
    }

    @Override
    public PathLike relativize(PathLike other) {
        return new JVMPath(path.relativize(toPath(other)));
    }

    @Override
    public Option<IOException> writeString(String content) {
        try {
            Files.writeString(path, content);
            return new None<>();
        } catch (IOException e) {
            return new Some<>(e);
        }
    }

    @Override
    public Option<IOException> createDirectories() {
        try {
            Files.createDirectories(path);
            return new None<>();
        } catch (IOException e) {
            return new Some<>(e);
        }
    }

    @Override
    public Result<Stream<PathLike>, IOException> walk() {
        try {
            return new Ok<>(Files.walk(path).map(JVMPath::new));
        } catch (IOException e) {
            return new Err<>(e);
        }
    }

    @Override
    public boolean exists() {
        return Files.exists(path);
    }

    @Override
    public Result<Stream<PathLike>, IOException> list() {
        try {
            return new Ok<>(Files.list(path).map(JVMPath::new));
        } catch (IOException e) {
            return new Err<>(e);
        }
    }

    @Override
    public Result<String, IOException> readString() {
        try {
            return new Ok<>(Files.readString(path));
        } catch (IOException e) {
            return new Err<>(e);
        }
    }

    @Override
    public Stream<String> streamNames() {
        final var root = path.getRoot();
        final Stream<String> rootStream = root == null ? Stream.empty() : Stream.of(root.toString());
        final var namesStream = IntStream.range(0, path.getNameCount())
                .mapToObj(path::getName)
                .map(Path::toString);

        return Stream.concat(rootStream, namesStream);
    }

    @Override
    public boolean isRegularFile() {
        return Files.isRegularFile(path);
    }
}
