package magma.app.io;

import magma.api.io.IOError;
import magma.api.io.path.PathLike;
import magma.api.optional.OptionalLike;

public record PathTargets(PathLike target) implements Targets {
    @Override
    public OptionalLike<IOError> write(final String output) {
        return this.target.writeString(output);
    }
}