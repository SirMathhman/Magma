package magma;

import magma.api.map.MapLike;
import magma.api.map.Maps;
import magma.app.Compiler;
import magma.app.PathLike;
import magma.app.jvm.JVMPaths;

import java.io.IOException;
import java.util.stream.Collectors;

public class Main {
    public static void main(String[] args) {
        try {
            final var root = JVMPaths.get(".", "src", "java");
            final var target = JVMPaths.get(".", "diagram.puml");
            run(root, target);
        } catch (IOException e) {
            //noinspection CallToPrintStackTrace
            e.printStackTrace();
        }
    }

    private static void run(PathLike root, PathLike target) throws IOException {
        final var sources = root.walk()
                .stream()
                .filter(PathLike::isRegularFile)
                .filter(path -> path.asString()
                        .endsWith(".java"))
                .collect(Collectors.toSet());

        final var output = compileAll(sources);
        target.writeString("@startuml\nskinparam linetype ortho\n" + output + "@enduml");
    }

    private static String compileAll(Iterable<PathLike> sources) throws IOException {
        final var sourceMap = readAll(sources);
        return Compiler.compile(sourceMap);
    }

    private static MapLike<String, String> readAll(Iterable<PathLike> sources) throws IOException {
        MapLike<String, String> sourceMap = Maps.empty();
        for (var source : sources) {
            final var fileName = source.getFileNameAsString();

            final var separator = fileName.lastIndexOf(".");
            final var name = fileName.substring(0, separator);
            final var input = source.readString();

            sourceMap = sourceMap.put(name, input);
        }
        return sourceMap;
    }
}
