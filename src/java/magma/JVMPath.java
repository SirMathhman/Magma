package magma;

import magma.option.None;
import magma.option.Option;
import magma.option.Some;

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
    public Stream<Path> walk() throws IOException {
        return Files.walk(path);
    }

    @Override
    public boolean exists() {
        return Files.exists(path);
    }

    @Override
    public Stream<Path> list() throws IOException {
        return Files.list(path);
    }

    @Override
    public String readString() throws IOException {
        return Files.readString(path);
    }

    @Override
    public Stream<String> streamNames() {
        return IntStream.range(0, path.getNameCount())
                .mapToObj(path::getName)
                .map(Path::toString);
    }
}
