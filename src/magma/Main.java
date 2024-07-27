package magma;

import magma.app.Application;
import magma.app.io.DirectorySourceSet;
import magma.app.io.DirectoryTargetSet;
import magma.app.ApplicationException;

import java.nio.file.Paths;

public class Main {
    public static void main(String[] args) {
        try {
            var sourceSet = new DirectorySourceSet(Paths.get(".", "src"));
            var targetSet = new DirectoryTargetSet(Paths.get(".", "dist"));
            var application = new Application(sourceSet, targetSet);
            application.run();
        } catch (ApplicationException e) {
            //noinspection CallToPrintStackTrace
            e.printStackTrace();
        }
    }
}
