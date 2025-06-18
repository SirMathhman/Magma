package magma.app;

import magma.api.Result;

import java.io.IOException;
import java.util.Optional;
import java.util.Set;

public interface PathLike {
    Result<String, IOException> readString();

    Optional<IOException> writeString(CharSequence content);

    boolean isRegularFile();

    Result<Set<PathLike>, IOException> walk();

    String getFileNameAsString();

    String asString();
}
