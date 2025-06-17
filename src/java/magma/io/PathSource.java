package magma.io;

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
    public String readString() throws IOException {
        return Files.readString(this.source);
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

        final var location = new SimpleLocation(namespace, name);
        return location;
    }
}