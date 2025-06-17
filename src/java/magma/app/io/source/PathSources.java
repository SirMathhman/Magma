package magma.app.io.source;

import jvm.list.JVMLists;
import magma.api.io.IOError;
import magma.api.io.PathLike;
import magma.api.list.ListLike;
import magma.api.result.Err;
import magma.api.result.Ok;
import magma.api.result.Result;

public record PathSources(PathLike root) implements Sources {
    @Override
    public Result<ListLike<Source>, IOError> collect() {
        return switch (this.root.walk()) {
            case Ok(var sources) -> this.filter(sources);
            case Err(var error) -> new Err<>(error);
        };
    }

    private Result<ListLike<Source>, IOError> filter(ListLike<PathLike> sources) {
        final var set = JVMLists.<Source>empty();
        for (var i = 0; i < sources.size(); i++) {
            final var source = sources.get(i);

            if (source.isRegularFile() && source.asString()
                    .endsWith(".java"))
                set.add(new PathSource(this.root, source));
        }

        return new Ok<>(set);
    }
}