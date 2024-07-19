package magma;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.stream.Stream;

public record PathSource(Path root, Path readableChild) {
    Stream<String> computeNamespace() {
        var parent = readableChild.getParent();
        if (parent == null) return Stream.empty();

        var relativized = root.relativize(parent);
        var namespace = new ArrayList<String>();
        for (int i = 0; i < relativized.getNameCount(); i++) {
            namespace.add(relativized.getName(i).toString());
        }

        return namespace.stream();
    }

    String computeName() {
        var fileName = readableChild().getFileName().toString();
        var separator = fileName.lastIndexOf('.');
        return fileName.substring(0, separator);
    }
}
