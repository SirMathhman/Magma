package magma.app.io.source;

import magma.api.collect.set.SetCollector;
import magma.api.collect.set.SetLike;
import magma.api.collect.stream.StreamLike;
import magma.api.io.IOError;
import magma.api.io.path.PathLike;
import magma.api.result.Result;

public record PathSources(PathLike root) implements Sources {
    private static SetLike<Source> filter(final StreamLike<PathLike> files) {
        return files.filter(PathLike::isRegularFile)
                .filter(path -> path.asString()
                        .endsWith(".java"))
                .map(PathSource::new)
                .collect(new SetCollector<>());
    }

    @Override
    public Result<SetLike<Source>, IOError> collect() {
        return this.root.walk()
                .mapValue(PathSources::filter);
    }
}