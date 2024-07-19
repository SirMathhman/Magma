package magma;

import java.nio.file.Paths;

public class Main {
    public static void main(String[] args) {
        try {
            new Application(new DirectorySourceSet(Paths.get(".")), new PathTargetSet()).run();
        } catch (CompileException e) {
            //noinspection CallToPrintStackTrace
            e.printStackTrace();
        }
    }
}
