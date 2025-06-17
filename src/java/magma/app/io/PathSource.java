package magma.app.io;

import magma.api.List;

import java.nio.file.Path;

public record PathSource(Path sourceDirectory, Path source) implements Source {
    @Override
    public String computeName() {
        final var fileName = this.source.getFileName()
                .toString();

        final var separator = fileName.lastIndexOf(".");
        return fileName.substring(0, separator);
    }

    @Override
    public List<String> computeNamespace() {
        final var relativeParent = this.sourceDirectory.relativize(this.source)
                .getParent();

        List<String> namespace = List.empty();
        for (var i = 0; i < relativeParent.getNameCount(); i++)
            namespace = namespace.add(relativeParent.getName(i)
                    .toString());
        return namespace;
    }
}