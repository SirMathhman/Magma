package magma;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class PathTargetSet implements TargetSet {
    static String resolve(String name, String extension) {
        return name + Compiler.IMPORT_SEPARATOR + extension;
    }

    static void writeSafe(Path target, String output) throws CompileException {
        try {
            Files.writeString(target, output);
        } catch (IOException e) {
            throw new CompileException(e);
        }
    }

    @Override
    public void write(Unit unit, String output) throws CompileException {
        var current = ApplicationTest.ROOT_PATH;

        var namespace = unit.computeNamespace();
        var name = unit.computeName();
        for (var segment : namespace.toList()) {
            current = current.resolve(segment);
        }

        var target = current.resolve(resolve(name, ApplicationTest.MAGMA_EXTENSION));
        writeSafe(target, output);
    }
}