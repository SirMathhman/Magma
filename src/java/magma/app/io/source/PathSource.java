package magma.app.io.source;

import magma.api.io.PathLike;
import magma.api.result.Result;
import magma.app.io.location.Location;
import magma.app.io.location.SimpleLocation;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public record PathSource(PathLike sourceDirectory, PathLike source) implements Source {
    private static List<String> computeNamespace(PathLike parent) {
        final List<String> segments = new ArrayList<>();
        for (var i = 0; i < parent.getNameCount(); i++)
            segments.add(parent.getName(i)
                    .asString());
        return segments;
    }

    @Override
    public Result<String, IOException> readString() {
        return this.source.readString();
    }

    @Override
    public Location computeLocation() {
        final var relative = this.sourceDirectory()
                .relativize(this.source);
        final var relativeParent = relative.getParent();

        final var segments = computeNamespace(relativeParent);
        final var namespace = String.join(".", segments);

        final var fileName = this.source.getFileName()
                .asString();
        final var separator = fileName.lastIndexOf(".");
        final var name = fileName.substring(0, separator);

        return new SimpleLocation(namespace, name);
    }
}