package magma;

import magma.api.Ok;
import magma.api.Result;
import magma.api.io.IOError;
import magma.api.io.JVMPaths;
import magma.api.io.PathLike;
import magma.api.map.MapLike;
import magma.api.map.Maps;
import magma.app.Compiler;
import magma.app.compile.Lang;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

public class Main {
    public static void main(String[] args) {
        final var root = JVMPaths.get(".", "src", "java");
        final var target = JVMPaths.get(".", "diagram.puml");
        run(root, target).ifPresent(error -> System.err.println(error.display()));
    }

    private static Optional<IOError> run(PathLike root, PathLike target) {
        return root.walk()
                .match(files -> runWithFiles(files, target), Optional::of);
    }

    private static Optional<IOError> runWithFiles(Collection<PathLike> files, PathLike target) {
        final var sources = files.stream()
                .filter(PathLike::isRegularFile)
                .filter(path -> path.asString()
                        .endsWith(".java"))
                .collect(Collectors.toSet());

        return compileAll(sources).match(output -> target.writeString("@startuml\nskinparam linetype ortho\n" + output + "@enduml"),
                Optional::of);
    }

    private static Result<String, IOError> compileAll(Iterable<PathLike> sources) {
        return readAll(sources).mapValue(sourceMap -> new Compiler(Lang.createJavaRootRule(),
                Lang.createPlantRootRule()).compile(sourceMap));
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
}
