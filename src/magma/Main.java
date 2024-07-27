package magma;

import java.io.IOException;
import java.nio.file.Paths;

public class Main {
    public static void main(String[] args) {
        try {
            var sourceSet = new DirectorySourceSet(Paths.get(".", "src"));
            var targetSet = new DirectoryTargetSet(Paths.get(".", "dist"));
            var application = new Application(sourceSet, targetSet);
            application.run();
        } catch (IOException e) {
            //noinspection CallToPrintStackTrace
            e.printStackTrace();
        }
    }
}
