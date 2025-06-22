package magma.app.io.targets;

import magma.api.error.Error;
import magma.api.error.WrappedError;
import magma.api.io.PathLike;
import magma.api.option.Option;

public record PathTargets(PathLike target) implements Targets {
    @Override
    public Option<Error> write(final String output) {
        return this.target.writeString(output)
                .map(WrappedError::new);
    }
}