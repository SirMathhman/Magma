package magma.app.io.source;

import magma.api.Tuple;
import magma.api.TupleImpl;
import magma.api.collect.map.MapCollector;
import magma.api.collect.map.MapLike;
import magma.api.collect.stream.ResultCollector;
import magma.api.io.IOError;
import magma.api.io.path.PathLike;
import magma.api.result.Result;

public record PathSources(PathLike root) implements Sources {
    private static Result<Tuple<String, String>, IOError> readSource(final Source source) {
        final var name = source.computeName();
        return source.read()
                .mapValue(input -> new TupleImpl<>(name, input));
    }

    @Override
    public Result<MapLike<String, String>, IOError> readSourceSet() {
        return this.root.walk()
                .flatMapValue(files -> files.filter(PathLike::isRegularFile)
                        .filter(path -> path.asString()
                                .endsWith(".java"))
                        .map(PathSource::new)
                        .map(PathSources::readSource)
                        .collect(new ResultCollector<>(new MapCollector<>())));
    }
}