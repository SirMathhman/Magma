package magma;

import magma.api.result.Err;
import magma.api.result.Ok;
import magma.api.result.Result;
import magma.app.ApplicationError;
import magma.app.Compiler;
import magma.app.ThrowableError;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class Main {
    public static void main(String[] args) {
        walk().mapErr(ThrowableError::new).mapErr(ApplicationError::new).match(Main::compileFiles, Optional::of).ifPresent(error -> {
            System.err.println(error.display());
        });
    }

    private static Result<Set<Path>, IOException> walk() {
        try (var stream = Files.walk(Paths.get(".", "src", "java"))) {
            final var collect = stream.collect(Collectors.toSet());
            return new Ok<>(collect);
        } catch (IOException e) {
            return new Err<>(e);
        }
    }

    private static Optional<ApplicationError> compileFiles(Set<Path> files) {
        final var sources = files.stream().filter(Files::isRegularFile).filter(path -> path.toString().endsWith(".java")).collect(Collectors.toSet());

        return compileSources(sources).match(output -> {
            final var target = Paths.get(".", "diagram.puml");
            return writeString(output, target).map(ThrowableError::new).map(ApplicationError::new);
        }, Optional::of);
    }

    private static Optional<IOException> writeString(String output, Path target) {
        try {
            Files.writeString(target, output);
            return Optional.empty();
        } catch (IOException e) {
            return Optional.of(e);
        }
    }

    private static Result<String, ApplicationError> compileSources(Set<Path> sources) {
        Result<StringBuilder, ApplicationError> maybeOutput = new Ok<>(new StringBuilder());
        for (var source : sources)
            maybeOutput = maybeOutput.and(() -> compileSource(source)).mapValue(tuple -> tuple.left().append(tuple.right()));

        return maybeOutput.mapValue(output -> {
            return "@startuml\nskinparam linetype ortho\n" + output + "@enduml";
        });
    }

    private static Result<String, ApplicationError> compileSource(Path source) {
        return readString(source).mapErr(ThrowableError::new).mapErr(ApplicationError::new).flatMapValue(input -> {
            final var fileName = source.getFileName().toString();
            final var name = fileName.substring(0, fileName.lastIndexOf("."));
            return Compiler.compile(input, name).mapValue(compiled -> {
                return "class " + name + "\n" + compiled;
            }).unwrap().mapErr(ApplicationError::new);
        });
    }

    private static Result<String, IOException> readString(Path source) {
        try {
            return new Ok<>(Files.readString(source));
        } catch (IOException error) {
            return new Err<>(error);
        }
    }
}