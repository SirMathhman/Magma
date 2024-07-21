package magma;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Collectors;

public record Application(SourceSet sourceSet) {
    public static final String MAGMA_EXTENSION = "mgs";

    private static void runWithPath(CompileUnit unit) throws IOException {
        writeTarget(unit);
    }

    private static void writeTarget(CompileUnit unit) throws IOException {
        var namespace = unit.computeNamespace().toList();
        var name = unit.computeName();

        var current = Paths.get(".");
        for (String segment : namespace) {
            current = current.resolve(segment);
        }

        Files.createFile(current.resolve(name + "." + MAGMA_EXTENSION));
    }

    void run() throws IOException {
        var unit = this.sourceSet()
                .streamPaths()
                .collect(Collectors.toSet());

        for (var path : unit) {
            runWithPath(path);
        }
    }
}