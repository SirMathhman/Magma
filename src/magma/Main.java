package magma;

import magma.app.Application;
import magma.app.ApplicationException;
import magma.app.io.DirectorySourceSet;
import magma.app.io.DirectoryTargetSet;

import java.nio.file.Paths;

public class Main {
    public static void main(String[] args) {
        try {
            var sourceSet = new DirectorySourceSet(Paths.get(".", "src"));
            var targetSet = new DirectoryTargetSet(Paths.get(".", "dist"));
            new Application(sourceSet, targetSet).run();
        } catch (ApplicationException e) {
            //noinspection CallToPrintStackTrace
            e.printStackTrace();
        }
    }
}