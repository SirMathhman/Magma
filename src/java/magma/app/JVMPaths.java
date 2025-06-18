package magma.app;

import java.nio.file.Path;
import java.nio.file.Paths;

public class JVMPaths {
    public static Path get(String first, String... more) {
        return Paths.get(first, more);
    }
}
