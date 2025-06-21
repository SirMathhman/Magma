package magma.app.io;

import magma.api.io.IOError;
import magma.api.io.path.PathLike;
import magma.api.result.Result;

public record PathSource(PathLike source) implements Source {
    @Override
    public Result<String, IOError> read() {
        return this.source.readString();
    }

    @Override
    public String computeName() {
        final var fileName = this.source.getFileName()
                .asString();

        final var separator = fileName.lastIndexOf('.');
        return fileName.substring(0, separator);
    }
}