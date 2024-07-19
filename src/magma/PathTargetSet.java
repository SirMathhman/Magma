package magma;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class PathTargetSet implements TargetSet {
    public static final String MAGMA_EXTENSION = "mgs";
    private final Path root;

    public PathTargetSet(Path root) {
        this.root = root;
    }

    static String resolve(String name, String extension) {
        return name + Compiler.IMPORT_SEPARATOR + extension;
    }

    static void writeSafe(Path target, String output) throws CompileException {
        try {
            var parent = target.getParent();
            if (parent != null && !Files.exists(parent)) {
                Files.createDirectories(parent);
            }

            Files.writeString(target, output);
        } catch (IOException e) {
            throw new CompileException(e);
        }
    }

    @Override
    public void write(Unit unit, String output) throws CompileException {
        var current = root;

        var namespace = unit.computeNamespace();
        var name = unit.computeName();
        for (var segment : namespace.toList()) {
            current = current.resolve(segment);
        }

        var target = current.resolve(resolve(name, MAGMA_EXTENSION));
        writeSafe(target, output);
    }
}