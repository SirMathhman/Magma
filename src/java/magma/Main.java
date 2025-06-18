package magma;

import magma.app.Compiler;
import magma.app.JVMFiles;
import magma.app.JVMPaths;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class Main {
    public static void main(String[] args) {
        try (final var stream = JVMFiles.walk(JVMPaths.get(".", "src", "java"))) {
            final var sources = stream.filter(JVMFiles::isRegularFile)
                    .filter(path -> path.toString()
                            .endsWith(".java"))
                    .collect(Collectors.toSet());

            final var output = compileAll(sources);
            JVMFiles.writeString(JVMPaths.get(".", "diagram.puml"),
                    "@startuml\nskinparam linetype ortho\n" + output + "@enduml");
        } catch (IOException e) {
            //noinspection CallToPrintStackTrace
            e.printStackTrace();
        }
    }

    private static String compileAll(Iterable<Path> sources) throws IOException {
        final var sourceMap = readAll(sources);
        return Compiler.compile(sourceMap);
    }

    private static Map<String, String> readAll(Iterable<Path> sources) throws IOException {
        final Map<String, String> sourceMap = new HashMap<>();
        for (var source : sources) {
            final var fileName = source.getFileName()
                    .toString();
            final var separator = fileName.lastIndexOf(".");
            final var name = fileName.substring(0, separator);
            final var input = JVMFiles.readString(source);

            sourceMap.put(name, input);
        }
        return sourceMap;
    }

}
