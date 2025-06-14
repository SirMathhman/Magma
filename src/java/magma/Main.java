package magma;

import magma.api.result.Err;
import magma.api.result.Ok;
import magma.api.result.Result;
import magma.app.Compiler;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class Main {
    public static void main(String[] args) {
        walk().match(Main::compileFiles, Optional::of).ifPresent(Throwable::printStackTrace);
    }

    private static Result<Set<Path>, IOException> walk() {
        try (var stream = Files.walk(Paths.get(".", "src", "java"))) {
            final var collect = stream.collect(Collectors.toSet());
            return new Ok<>(collect);
        } catch (IOException e) {
            return new Err<>(e);
        }
    }

    private static Optional<IOException> compileFiles(Set<Path> files) {
        final var sources = files.stream().filter(Files::isRegularFile).filter(path -> path.toString().endsWith(".java")).collect(Collectors.toSet());

        return compileSources(sources).match(output -> {
            final var target = Paths.get(".", "diagram.puml");
            return writeString(output, target);
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

    private static Result<String, IOException> compileSources(Set<Path> sources) {
        Result<StringBuilder, IOException> maybeOutput = new Ok<>(new StringBuilder());
        for (var source : sources)
            maybeOutput = maybeOutput.and(() -> compileSource(source)).mapValue(tuple -> tuple.left().append(tuple.right()));

        return maybeOutput.mapValue(output -> {
            return "@startuml\nskinparam linetype ortho\n" + output + "@enduml";
        });
    }

    private static Result<String, IOException> compileSource(Path source) {
        return readString(source).mapValue(input -> {
            final var fileName = source.getFileName().toString();
            final var name = fileName.substring(0, fileName.lastIndexOf("."));
            return "class " + name + "\n" + Compiler.compile(input, name);
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