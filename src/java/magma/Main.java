package magma;

import magma.app.Compiler;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        try (final var stream = Files.walk(Paths.get(".", "src", "java"))) {
            final var sources = stream.filter(Files::isRegularFile).filter(path -> path.toString().endsWith(".java")).toList();
            final var output = compileSources(sources);
            final var target = Paths.get(".", "diagram.puml");
            Files.writeString(target, "@startuml\nskinparam linetype ortho\n" + output + "@enduml");
        } catch (IOException e) {
            //noinspection CallToPrintStackTrace
            e.printStackTrace();
        }
    }

    private static String compileSources(List<Path> sources) throws IOException {
        final var output = new StringBuilder();
        for (var source : sources)
            output.append(compileSource(source));
        return output.toString();
    }

    private static String compileSource(Path source) throws IOException {
        final var fileName = source.getFileName().toString();
        final var separator = fileName.lastIndexOf(".");
        final var name = fileName.substring(0, separator);

        final var input = Files.readString(source);
        final var result = Compiler.compileRoot(input, name);

        return "class " + name + "\n" + result;
    }
}
