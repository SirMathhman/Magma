package magma;

import magma.app.ApplicationError;
import magma.api.ThrowableError;
import magma.app.ApplicationResult;
import magma.app.Compiler;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

public class Main {
    public static void main(String[] args) {
        run().ifPresent(error -> {
            System.err.println(error.display());
        });
    }

    private static Optional<ApplicationError> run() {
        try (final var stream = Files.walk(Paths.get(".", "src", "java"))) {
            final var sources = stream.filter(Files::isRegularFile).filter(path -> path.toString().endsWith(".java")).toList();
            return compileSources(sources).extract(Main::writeOutput);
        } catch (IOException e) {
            return Optional.of(new ApplicationError(new ThrowableError(e)));
        }
    }

    private static Optional<ApplicationError> writeOutput(String output) {
        try {
            final var target = Paths.get(".", "diagram.puml");
            Files.writeString(target, "@startuml\nskinparam linetype ortho\n" + output + "@enduml");
            return Optional.empty();
        } catch (IOException e) {
            return Optional.of(new ApplicationError(new ThrowableError(e)));
        }
    }

    private static ApplicationResult compileSources(List<Path> sources) throws IOException {
        ApplicationResult output = new ApplicationResult.Ok("");
        for (var source : sources)
            output = output.append(() -> compileSource(source));
        return output;
    }

    private static ApplicationResult compileSource(Path source) {
        final var fileName = source.getFileName().toString();
        final var separator = fileName.lastIndexOf(".");
        final var name = fileName.substring(0, separator);
        return readString(source).compile(input1 -> complete(input1, name));
    }

    private static ApplicationResult complete(String input1, String name) {
        return Compiler.compileRoot(input1, name).prependString("class " + name + "\n").toApplicationResult();
    }

    private static ApplicationResult readString(Path source) {
        try {
            final var input = Files.readString(source);
            return new ApplicationResult.Ok(input);
        } catch (IOException e) {
            return new ApplicationResult.Err(new ApplicationError(new ThrowableError(e)));
        }
    }
}
