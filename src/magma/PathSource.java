package magma;

import java.nio.file.Path;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public record PathSource(Path root, Path source) implements Source {
    public static final String EXTENSION_SEPARATOR = ".";

    @Override
    public String computeName() {
        var fileName = source().getFileName().toString();
        var separator = fileName.indexOf(EXTENSION_SEPARATOR);
        return fileName.substring(0, separator);
    }

    @Override
    public Stream<String> computeNamespace() {
        var relativized = root.relativize(source);
        var parent = relativized.getParent();
        if (parent == null) return Stream.empty();

        var count = parent.getNameCount();
        return IntStream.range(0, count)
                .mapToObj(parent::getName)
                .map(Path::toString);
    }
}