package magma.api.io;

import magma.api.result.Result;

import java.io.IOException;
import java.util.Optional;
import java.util.Set;

public interface PathLike {
    Optional<IOException> writeString(CharSequence output);

    Result<Set<PathLike>, IOException> walk();

    Result<String, IOException> readString();

    int getNameCount();

    PathLike getName(int index);

    String asString();

    PathLike relativize(PathLike child);

    PathLike getParent();

    PathLike getFileName();

    boolean isRegularFile();
}
