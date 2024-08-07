package magma.app;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public record PathUnit(Path root, Path child) implements Unit {
    @Override
    public String read() throws IOException {
        return Files.readString(child);
    }

    @Override
    public String computeName() {
        var fileName = child.getFileName().toString();
        var separator = fileName.lastIndexOf('.');
        return fileName.substring(0, separator);
    }

    @Override
    public Stream<String> computeNamespace() {
        var relativized = root.relativize(child.getParent());
        return IntStream.range(0, relativized.getNameCount())
                .mapToObj(relativized::getName)
                .map(Path::toString);
    }
}