package magma;

public class Paths {
    public static PathLike get(final String first, final String... more) {
        return new JavaPath(java.nio.file.Paths.get(first, more));
    }
}
