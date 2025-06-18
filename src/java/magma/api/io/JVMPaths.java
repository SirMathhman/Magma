package magma.api.io;

import magma.app.PathLike;

import java.nio.file.Paths;

public class JVMPaths {
    public static PathLike get(String first, String... more) {
        return new JVMPath(Paths.get(first, more));
    }
}
