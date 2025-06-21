package magma.app;

import magma.api.Tuple;
import magma.api.collect.map.MapCollector;
import magma.api.collect.map.MapLike;
import magma.api.collect.set.SetCollector;
import magma.api.collect.set.SetLike;
import magma.api.collect.stream.ResultCollector;
import magma.api.collect.stream.StreamLike;
import magma.api.io.IOError;
import magma.api.io.path.PathLike;
import magma.api.io.path.PathLikes;
import magma.api.optional.OptionalLike;
import magma.api.optional.Optionals;
import magma.api.result.Result;
import magma.app.compile.Compiler;
import magma.app.compile.lang.Lang;

class Main {
    private Main() {
    }

    public static void main(final String[] args) {
        final var sourceRoot = PathLikes.get(".", "src", "java");
        sourceRoot.walk()
                .mapValue(Main::filter)
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
                .map(Main::readSource)
                .collect(new ResultCollector<>(new MapCollector<>()));
    }

    private static Result<Tuple<String, String>, IOError> readSource(final PathLike source) {
        final var fileName = source.getFileName()
                .asString();

        final var separator = fileName.lastIndexOf('.');
        final var name = fileName.substring(0, separator);

        return source.readString()
                .mapValue(input -> new Tuple<>(name, input));
    }


    private static SetLike<PathLike> filter(final StreamLike<PathLike> files) {
        return files.filter(PathLike::isRegularFile)
                .filter(path -> path.asString()
                        .endsWith(".java"))
                .collect(new SetCollector<>());
    }
}
