package magma.app;

import magma.api.Ok;
import magma.api.Result;
import magma.api.io.error.IOError;
import magma.api.io.path.PathLike;
import magma.api.map.MapLike;
import magma.api.map.Maps;
import magma.app.compile.Compiler;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public record CompileApplication(PathLike root, PathLike target, Compiler compiler) implements Application {
    private static Result<String, IOError> compileAll(Iterable<PathLike> sources, Compiler compiler) {
        return readAll(sources).mapValue(compiler::compile);
    }

    private static Result<MapLike<String, String>, IOError> readAll(Iterable<PathLike> sources) {
        Result<MapLike<String, String>, IOError> maybeSourceMap = new Ok<>(Maps.empty());
        for (var source : sources) {
            final var fileName = source.getFileNameAsString();

            final var separator = fileName.lastIndexOf(".");
            final var name = fileName.substring(0, separator);
            final var maybeInput = source.readString();

            maybeSourceMap = maybeSourceMap.flatMapValue(sourceMap -> maybeInput.mapValue(input -> sourceMap.put(name,
                    input)));
        }

        return maybeSourceMap;
    }

    @Override
    public Optional<IOError> run() {
        return this.root.walk()
                .match(this::runWithFiles, Optional::of);
    }

    private Optional<IOError> runWithFiles(Collection<PathLike> files) {
        final var sources = this.filter(files);
        return compileAll(sources, this.compiler).match(output -> this.target.writeString(
                "@startuml\nskinparam linetype ortho\n" + output + "@enduml"), Optional::of);
    }

    private Set<PathLike> filter(Collection<PathLike> files) {
        return files.stream()
                .filter(PathLike::isRegularFile)
                .filter(path -> path.asString()
                        .endsWith(".java"))
                .collect(Collectors.toSet());
    }
}