package magma;

import magma.api.Ok;
import magma.api.Result;
import magma.api.map.MapLike;
import magma.api.map.Maps;
import magma.app.Compiler;
import magma.app.PathLike;
import magma.app.jvm.JVMPaths;

import java.io.IOException;
import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

public class Main {
    public static void main(String[] args) {
        final var root = JVMPaths.get(".", "src", "java");
        final var target = JVMPaths.get(".", "diagram.puml");
        run(root, target).ifPresent(Throwable::printStackTrace);
    }

    private static Optional<IOException> run(PathLike root, PathLike target) {
        return root.walk()
                .match(files -> runWithFiles(files, target), Optional::of);
    }

    private static Optional<IOException> runWithFiles(Collection<PathLike> files, PathLike target) {
        final var sources = files.stream()
                .filter(PathLike::isRegularFile)
                .filter(path -> path.asString()
                        .endsWith(".java"))
                .collect(Collectors.toSet());

        return compileAll(sources).match(output -> target.writeString("@startuml\nskinparam linetype ortho\n" + output + "@enduml"),
                Optional::of);
    }

    private static Result<String, IOException> compileAll(Iterable<PathLike> sources) {
        return readAll(sources).mapValue(Compiler::compile);
    }

    private static Result<MapLike<String, String>, IOException> readAll(Iterable<PathLike> sources) {
        Result<MapLike<String, String>, IOException> maybeSourceMap = new Ok<>(Maps.empty());
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
