package magma;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Collectors;

public record Application(SingleSourceSet sourceSet) {
    public static final String MAGMA_EXTENSION = "mgs";

    private static void runWithPath(Path source) throws IOException {
        var fileName = source.getFileName().toString();
        var separator = fileName.indexOf('.');
        var name = fileName.substring(0, separator);
        Files.createFile(source.resolveSibling(name + "." + MAGMA_EXTENSION));
    }

    void run() throws IOException {
        var paths = sourceSet()
                .streamPaths()
                .collect(Collectors.toSet());

        for (Path path : paths) {
            runWithPath(path);
        }
    }
}