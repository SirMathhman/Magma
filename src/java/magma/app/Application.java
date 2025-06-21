package magma.app;

import magma.api.Tuple;
import magma.api.TupleImpl;
import magma.api.collect.map.MapCollector;
import magma.api.collect.set.SetLike;
import magma.api.collect.stream.ResultCollector;
import magma.api.io.IOError;
import magma.api.io.path.PathLikes;
import magma.api.optional.OptionalLike;
import magma.api.optional.Optionals;
import magma.api.result.Result;
import magma.app.compile.Compiler;
import magma.app.compile.lang.Lang;
import magma.app.io.Source;
import magma.app.io.Sources;

public class Application {
    private Application() {
    }

    public static OptionalLike<IOError> run(final Sources sources) {
        return sources.collect()
                .flatMapValue(Application::compileSources)
                .match(Application::write, Optionals::of);
    }

    private static OptionalLike<IOError> write(final String output) {
        final var target = PathLikes.get(".", "diagram.puml");
        final var joined = String.join(Lang.SEPARATOR, "@startuml", "skinparam linetype ortho", output, "@enduml");
        return target.writeString(joined);
    }

    private static Result<String, IOError> compileSources(final SetLike<Source> sources) {
        return sources.stream()
                .map(Application::readSource)
                .collect(new ResultCollector<>(new MapCollector<>()))
                .mapValue(Compiler::compileEntries);
    }

    private static Result<Tuple<String, String>, IOError> readSource(final Source source) {
        final var name = source.computeName();
        return source.read()
                .mapValue(input -> new TupleImpl<>(name, input));
    }
}