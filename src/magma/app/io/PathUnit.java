package magma.app.io;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public record PathUnit(Path root, Path readableChild) implements Unit {
    @Override
    public Stream<String> computeNamespace() {
        var parent = readableChild.getParent();
        if (parent == null) return Stream.empty();

        var relativized = root.relativize(parent);
        var length = relativized.getNameCount();
        return IntStream.range(0, length)
                .mapToObj(relativized::getName)
                .map(Path::toString);
    }

    @Override
    public String computeName() {
        var fileName = readableChild().getFileName().toString();
        var separator = fileName.lastIndexOf('.');
        return separator == -1
                ? fileName
                : fileName.substring(0, separator);
    }

    @Override
    public String read() throws IOException {
        return Files.readString(readableChild);
    }
}
