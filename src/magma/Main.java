package magma;

import magma.app.Application;
import magma.app.ApplicationException;
import magma.app.DirectorySourceSet;
import magma.app.PathTargetSet;

import java.nio.file.Paths;

public class Main {
    public static void main(String[] args) {
        try {
            var sourceDirectory = Paths.get(".", "src");
            var targetDirectory = Application.ROOT_DIRECTORY.resolve("dist");

            var sourceSet = new DirectorySourceSet(sourceDirectory, "java");
            var targetSet = new PathTargetSet(targetDirectory);
            var application = new Application(sourceSet, targetSet);
            var run = application.run();
            if (run.isPresent()) throw run.get();
        } catch (ApplicationException e) {
            //noinspection CallToPrintStackTrace
            e.printStackTrace();
        }
    }
}
