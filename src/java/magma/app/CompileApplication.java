package magma.app;

import magma.api.Tuple;
import magma.api.TupleImpl;
import magma.api.collect.map.MapCollector;
import magma.api.collect.set.SetLike;
import magma.api.collect.stream.ResultCollector;
import magma.api.io.IOError;
import magma.api.optional.OptionalLike;
import magma.api.optional.Optionals;
import magma.api.result.Result;
import magma.app.compile.Compiler;
import magma.app.compile.lang.Lang;
import magma.app.io.source.Source;
import magma.app.io.source.Sources;
import magma.app.io.target.Targets;

public record CompileApplication(Sources sources, Targets targets) implements Application {
    private static Result<String, IOError> compileSources(final SetLike<Source> sources) {
        return sources.stream()
                .map(CompileApplication::readSource)
                .collect(new ResultCollector<>(new MapCollector<>()))
                .mapValue(Compiler::compileEntries);
    }

    private static Result<Tuple<String, String>, IOError> readSource(final Source source) {
        final var name = source.computeName();
        return source.read()
                .mapValue(input -> new TupleImpl<>(name, input));
    }

    @Override
    public OptionalLike<IOError> run() {
        return this.sources.collect()
                .flatMapValue(CompileApplication::compileSources)
                .match(this::write, Optionals::of);
    }

    private OptionalLike<IOError> write(final String output) {
        return this.targets.write(String.join(Lang.SEPARATOR,
                "@startuml",
                "skinparam linetype ortho",
                output,
                "@enduml"));
    }
}