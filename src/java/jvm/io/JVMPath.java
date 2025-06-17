package jvm.io;

import jvm.list.JVMList;
import magma.api.io.IOError;
import magma.api.io.IOOption;
import magma.api.io.PathLike;
import magma.api.io.SimpleIOOption;
import magma.api.list.Sequence;
import magma.api.result.Err;
import magma.api.result.Ok;
import magma.api.result.Result;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Collectors;

public record JVMPath(Path path) implements PathLike {
    @Override
    public IOOption writeString(CharSequence output) {
        try {
            Files.writeString(this.path, output);
            return SimpleIOOption.empty();
        } catch (IOException e) {
            return SimpleIOOption.of(new JVMIOError(e));
        }
    }

    @Override
    public Result<Sequence<PathLike>, IOError> walk() {
        try (final var stream = Files.walk(this.path)) {
            return new Ok<>(new JVMList<>(stream.map(JVMPath::new)
                    .collect(Collectors.toList())));
        } catch (IOException e) {
            return new Err<>(new JVMIOError(e));
        }
    }

    @Override
    public Result<String, IOError> readString() {
        try {
            return new Ok<>(Files.readString(this.path));
        } catch (IOException e) {
            return new Err<>(new JVMIOError(e));
        }
    }

    @Override
    public int getNameCount() {
        return this.path.getNameCount();
    }

    @Override
    public PathLike getName(int index) {
        return new JVMPath(this.path.getName(index));
    }

    @Override
    public String asString() {
        return this.path.toString();
    }

    @Override
    public PathLike relativize(PathLike child) {
        if (child instanceof JVMPath(var inner))
            return new JVMPath(this.path.relativize(inner));

        return this;
    }

    @Override
    public PathLike getParent() {
        return new JVMPath(this.path.getParent());
    }

    @Override
    public PathLike getFileName() {
        return new JVMPath(this.path.getFileName());
    }

    @Override
    public boolean isRegularFile() {
        return Files.isRegularFile(this.path);
    }
}
