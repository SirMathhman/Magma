package magma;

import magma.option.None;
import magma.option.Option;
import magma.option.Some;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

/** Wrapper implementation backed by {@code java.nio.file.Path}. */
public record JVMPath(java.nio.file.Path path) implements PathLike {
    @Override
    public java.nio.file.Path unwrap() {
        return path;
    }

    @Override
    public String toString() {
        return path.toString();
    }

    @Override
    public PathLike resolve(String other) {
        return new JVMPath(unwrap().resolve(other));
    }

    @Override
    public PathLike resolve(PathLike other) {
        return new JVMPath(unwrap().resolve(other.unwrap()));
    }

    @Override
    public PathLike getParent() {
        Path parent = unwrap().getParent();
        return new JVMPath(parent);
    }

    @Override
    public PathLike relativize(PathLike other) {
        return new JVMPath(unwrap().relativize(other.unwrap()));
    }

    @Override
    public Option<IOException> writeString(String content) {
        try {
            Files.writeString(unwrap(), content);
            return new None<>();
        } catch (IOException e) {
            return new Some<>(e);
        }
    }

    @Override
    public Option<IOException> createDirectories() {
        try {
            Files.createDirectories(unwrap());
            return new None<>();
        } catch (IOException e) {
            return new Some<>(e);
        }
    }

    @Override
    public Stream<Path> walk() throws IOException {
        return Files.walk(unwrap());
    }
}
