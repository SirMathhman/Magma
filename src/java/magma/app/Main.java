package magma.app;

import magma.api.result.Err;
import magma.api.result.Ok;
import magma.api.result.Result;
import magma.app.compile.CompilerImpl;
import magma.app.io.source.Source;
import magma.app.io.source.Sources;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class Main {
    public static void main(String[] args) {
        run().ifPresent(Throwable::printStackTrace);
    }

    private static Optional<IOException> run() {
        return switch (new Sources(Paths.get(".", "src", "java")).collect()) {
            case Err<Set<Source>, IOException>(var error) -> Optional.of(error);
            case Ok<Set<Source>, IOException>(var files) -> compileAll(files);
        };
    }

    private static Optional<IOException> compileAll(Iterable<Source> sources) {
        return switch (readAll(sources)) {
            case Err(var error) -> Optional.of(error);
            case Ok(var sourceMap) -> compileAndWrite(sourceMap);
        };
    }

    private static Optional<IOException> compileAndWrite(Map<Source, String> sourceMap) {
        final var fileName = new CompilerImpl().compile(sourceMap);
        final var path = Paths.get(".", "diagram.puml");
        final var output = "@startuml\nskinparam linetype ortho\n" + fileName + "@enduml";
        return writeString(path, output);
    }

    private static Result<Map<Source, String>, IOException> readAll(Iterable<Source> sources) {
        final var sourceMap = new HashMap<Source, String>();
        for (var source : sources) {
            final var result = source.readString();
            switch (result) {
                case Err<String, IOException> error -> {
                    return new Err<>(error.error());
                }
                case Ok<String, IOException> value -> {
                    sourceMap.put(source, value.value());
                }
            }
        }

        return new Ok<>(sourceMap);
    }

    private static Optional<IOException> writeString(Path path, String output) {
        try {
            Files.writeString(path, output);
            return Optional.empty();
        } catch (IOException e) {
            return Optional.of(e);
        }
    }

}
