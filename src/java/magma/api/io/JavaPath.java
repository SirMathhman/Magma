package magma.api.io;

import magma.api.list.JavaList;
import magma.api.list.ListLike;
import magma.api.option.None;
import magma.api.option.Option;
import magma.api.option.Some;
import magma.api.result.Err;
import magma.api.result.Ok;
import magma.api.result.Result;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

record JavaPath(Path path) implements PathLike {
    @Override
    public PathLike getFileName() {
        return new JavaPath(this.path.getFileName());
    }

    @Override
    public String asString() {
        return this.path.toString();
    }

    @Override
    public Option<IOError> writeString(final CharSequence output) {
        try {
            Files.writeString(this.path, output);
            return new None<>();
        } catch (final IOException e) {
            return new Some<>(new JavaIOError(e));
        }
    }

    @Override
    public Result<ListLike<PathLike>, IOError> walk() {
        try (final var stream = Files.walk(this.path)) {
            return new Ok<>(new JavaList<>(stream.<PathLike>map(JavaPath::new)
                    .toList()));
        } catch (final IOException e) {
            return new Err<>(new JavaIOError(e));
        }
    }

    @Override
    public Result<String, IOError> readString() {
        try {
            return new Ok<>(Files.readString(this.path));
        } catch (final IOException e) {
            return new Err<>(new JavaIOError(e));
        }
    }
}
