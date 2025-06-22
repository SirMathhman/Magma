package magma.path;

import java.nio.file.Path;

public interface PathLike {
    PathLike getFileName();

    Path unwrap();

    String asString();
}
