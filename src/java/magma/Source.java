package magma;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;

public record Source(Path sourceDirectory, Path source) {
    String computeName() {
        final var fileName = this.source.getFileName()
                .toString();

        final var separator = fileName.lastIndexOf(".");
        return fileName.substring(0, separator);
    }

    Collection<String> computeNamespace() {
        final var relativeParent = this.sourceDirectory.relativize(this.source)
                .getParent();

        final Collection<String> namespace = new ArrayList<>();
        for (var i = 0; i < relativeParent.getNameCount(); i++)
            namespace.add(relativeParent.getName(i)
                    .toString());
        return namespace;
    }
}