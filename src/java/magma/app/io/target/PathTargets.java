package magma.app.io.target;

import magma.api.io.IOError;
import magma.api.io.path.PathLike;
import magma.api.optional.Option;

public record PathTargets(PathLike target) implements Targets {
    @Override
    public Option<IOError> write(final String output) {
        return this.target.writeString(output);
    }
}