package magma;

import magma.app.Application;
import magma.app.ApplicationException;
import magma.app.io.DirectorySourceSet;
import magma.app.io.PathTargetSet;

import java.nio.file.Path;
import java.nio.file.Paths;

public class Main {
    public static final Path SOURCE_DIRECTORY = Paths.get(".", "src");
    public static final Path TARGET_DIRECTORY = Paths.get(".", "dist");

    public static void main(String[] args) {
        try {
            var sourceSet = new DirectorySourceSet(SOURCE_DIRECTORY);
            var targetSet = new PathTargetSet(TARGET_DIRECTORY);
            new Application(sourceSet, targetSet).run();
        } catch (ApplicationException e) {
            //noinspection CallToPrintStackTrace
            e.printStackTrace();
        }
    }
}
