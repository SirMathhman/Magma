package magma.app.io.source;

import magma.api.result.Err;
import magma.api.result.Ok;
import magma.api.result.Result;
import magma.app.io.location.Location;
import magma.app.io.location.SimpleLocation;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public record PathSource(Path sourceDirectory, Path source) implements Source {
    private static List<String> computeNamespace(Path parent) {
        final List<String> segments = new ArrayList<>();
        for (var i = 0; i < parent.getNameCount(); i++)
            segments.add(parent.getName(i)
                    .toString());
        return segments;
    }

    @Override
    public Result<String, IOException> readString() {
        try {
            return new Ok<>(Files.readString(this.source));
        } catch (IOException e) {
            return new Err<>(e);
        }
    }

    @Override
    public Location computeLocation() {
        final var relative = this.sourceDirectory()
                .relativize(this.source);
        final var relativeParent = relative.getParent();

        final var segments = computeNamespace(relativeParent);
        final var namespace = String.join(".", segments);

        final var fileName = this.source.getFileName()
                .toString();
        final var separator = fileName.lastIndexOf(".");
        final var name = fileName.substring(0, separator);

        return new SimpleLocation(namespace, name);
    }
}