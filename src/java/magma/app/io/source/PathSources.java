package magma.app.io.source;

import magma.api.result.Err;
import magma.api.result.Ok;
import magma.api.result.Result;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;
import java.util.stream.Collectors;

public record PathSources(Path sourceDirectory) implements Sources {
    @Override
    public Result<Set<Source>, IOException> collect() {
        try (final var stream = Files.walk(this.sourceDirectory)) {
            return new Ok<>(stream.filter(Files::isRegularFile)
                    .filter(path -> path.toString()
                            .endsWith(".java"))
                    .map(source -> new PathSource(this.sourceDirectory, source))
                    .collect(Collectors.toSet()));
        } catch (IOException e) {
            return new Err<>(e);
        }
    }
}