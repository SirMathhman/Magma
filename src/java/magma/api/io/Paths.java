package magma.api.io;

public class Paths {
    public static PathLike get(String first, String... more) {
        return new JavaPath(java.nio.file.Paths.get(first, more));
    }
}
