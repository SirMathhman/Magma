package magma.app;

import magma.api.Tuple;
import magma.api.collect.map.MapCollector;
import magma.api.collect.map.MapLike;
import magma.api.collect.set.SetLike;
import magma.api.collect.stream.ResultCollector;
import magma.api.io.IOError;
import magma.api.io.path.PathLike;
import magma.api.io.path.PathLikes;
import magma.api.optional.OptionalLike;
import magma.api.optional.Optionals;
import magma.api.result.Result;
import magma.app.compile.Compiler;
import magma.app.compile.lang.Lang;
import magma.app.io.PathSource;
import magma.app.io.PathSources;
import magma.app.io.Source;
import magma.app.io.Sources;

class Main {
    private Main() {
    }

    public static void main(final String[] args) {
        final Sources sources = new PathSources(PathLikes.get(".", "src", "java"));
        sources.collect()
                .flatMapValue(Main::compileSources)
                .match(Main::write, Optionals::of)
                .ifPresent(error -> System.err.println(error.display()));
    }

    private static OptionalLike<IOError> write(final String output) {
        final var target = PathLikes.get(".", "diagram.puml");
        final var joined = String.join(Lang.SEPARATOR, "@startuml", "skinparam linetype ortho", output, "@enduml");
        return target.writeString(joined);
    }

    private static Result<String, IOError> compileSources(final SetLike<PathLike> sources) {
        return Main.readSources(sources)
                .mapValue(Compiler::compileEntries);
    }

    private static Result<MapLike<String, String>, IOError> readSources(final SetLike<PathLike> sources) {
        return sources.stream()
                .map(source -> Main.readSource(new PathSource(source)))
                .collect(new ResultCollector<>(new MapCollector<>()));
    }

    private static Result<Tuple<String, String>, IOError> readSource(final Source source) {
        final var name = source.computeName();
        return source.read()
                .mapValue(input -> new Tuple<>(name, input));
    }
}
