package magma.api.io;

import java.nio.file.Path;

public class Paths {
    public static Path get(String first, String... more) {
        return java.nio.file.Paths.get(first, more);
    }
}
