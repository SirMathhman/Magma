package magma.app.io;

import magma.app.Application;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class DirectoryTargetSet implements TargetSet {
    private final Path root;

    public DirectoryTargetSet(Path root) {
        this.root = root;
    }

    @Override
    public void writeTarget(Unit unit, String output) throws IOException {
        var namespace = unit.computeNamespace().toList();
        var name = unit.computeName();

        var parent = root;
        for (String segment : namespace) {
            parent = parent.resolve(segment);
        }

        if (!Files.exists(parent)) Files.createDirectories(parent);
        Files.createFile(parent.resolve(name + "." + Application.MAGMA_EXTENSION));
    }
}