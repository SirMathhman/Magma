package magma;

import java.io.IOException;
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