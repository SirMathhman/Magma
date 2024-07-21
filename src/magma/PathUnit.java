package magma;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public record PathUnit(Path root, Path child) implements CompileUnit {
    @Override
    public String computeName() {
        var fileName = child().getFileName().toString();
        var separator = fileName.indexOf('.');
        return fileName.substring(0, separator);
    }

    @Override
    public Stream<String> computeNamespace() {
        var parent = child.getParent();
        if(parent == null) return Stream.empty();

        var relativized = root.relativize(parent);
        return IntStream.range(0, relativized.getNameCount())
                .mapToObj(relativized::getName)
                .map(Path::toString);
    }

    @Override
    public String read() throws IOException {
        return Files.readString(child);
    }
}