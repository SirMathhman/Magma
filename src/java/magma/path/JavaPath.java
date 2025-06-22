package magma.path;

import magma.error.IOError;
import magma.error.JavaIOError;
import magma.list.JavaList;
import magma.list.ListLike;
import magma.option.Option;
import magma.result.Err;
import magma.result.Ok;
import magma.result.Result;

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
            return Option.empty();
        } catch (final IOException e) {
            return Option.of(new JavaIOError(e));
        }
    }

    @Override
    public Result<ListLike<PathLike>> walk() {
        try (final var stream = Files.walk(this.path)) {
            return new Ok<>(new JavaList<>(stream.<PathLike>map(JavaPath::new)
                    .toList()));
        } catch (final IOException e) {
            return new Err<>(new JavaIOError(e));
        }
    }

    @Override
    public Result<String> readString() {
        try {
            return new Ok<>(Files.readString(this.path));
        } catch (final IOException e) {
            return new Err<>(new JavaIOError(e));
        }
    }
}
