package magma;

import java.nio.file.Path;
import java.nio.file.Paths;

public class Main {

    public static final Path SOURCE_DIRECTORY = Paths.get(".", "src");

    public static void main(String[] args) {
        try {
            new Application(new DirectorySourceSet(SOURCE_DIRECTORY), new PathTargetSet(Paths.get("."))).run();
        } catch (CompileException e) {
            //noinspection CallToPrintStackTrace
            e.printStackTrace();
        }
    }
}
