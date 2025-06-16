package magma.app;

import magma.api.Err;
import magma.api.Error;
import magma.api.Ok;
import magma.api.Result;
import magma.api.ThrowableError;
import magma.app.compile.Compiler;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class Application {
    private final Compiler compiler;

    public Application(Compiler compiler) {
        this.compiler = compiler;
    }

    Result<Set<Path>, Error> collect() {
        try (final var stream = Files.walk(Paths.get(".", "src", "java"))) {
            final var sources = stream.filter(Files::isRegularFile)
                    .filter(path -> path.toString()
                            .endsWith(".java"))
                    .collect(Collectors.toSet());

            return new Ok<>(sources);
        } catch (IOException e) {
            return new Err<>(new ThrowableError(e));
        }
    }

    Optional<Error> writeString(Path path, CharSequence content) {
        try {
            Files.writeString(path, content);
            return Optional.empty();
        } catch (IOException e) {
            return Optional.of(new ThrowableError(e));
        }
    }

    Result<Map<String, String>, Error> readSource(Path source) {
        try {
            final var fileName = source.getFileName()
                    .toString();

            final var extensionSeparator = fileName.lastIndexOf(".");
            final var name = fileName.substring(0, extensionSeparator);
            final var input = Files.readString(source);
            return new Ok<>(Map.of(name, input));
        } catch (IOException e) {
            return new Err<>(new ThrowableError(e));
        }
    }

    Result<Map<String, String>, Error> readAll(Iterable<Path> sources) {
        Result<Map<String, String>, Error> inputs = new Ok<>(new HashMap<>());
        for (var source : sources)
            inputs = inputs.flatMap(inner -> this.readSource(source)
                    .mapValue(inner0 -> {
                        inner.putAll(inner0);
                        return inner;
                    }));
        return inputs;
    }

    public Optional<? extends Error> run() {
        return this.collect()
                .match(sources -> this.readAll(sources)
                        .match(this::compileAndWrite, Optional::of), Optional::of);
    }

    Optional<? extends Error> compileAndWrite(Map<String, String> inputs) {
        return this.compiler.compile(inputs)
                .toResult()
                .match(output -> this.writeString(Paths.get(".", "diagram.puml"), output), Optional::of);
    }
}