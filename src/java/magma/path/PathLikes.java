package magma.path;

import java.nio.file.Paths;

public class PathLikes {
    private PathLikes() {
    }

    public static PathLike get(final String first, final String... more) {
        return new JavaPath(Paths.get(first, more));
    }
}
