package magma.api.io.path;

import java.nio.file.Paths;

public class JVMPaths {
    public static PathLike get(String first, String... more) {
        return new JVMPath(Paths.get(first, more));
    }
}
