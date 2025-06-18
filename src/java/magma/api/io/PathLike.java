package magma.api.io;

import magma.api.Result;

import java.util.Optional;
import java.util.Set;

public interface PathLike {
    Result<String, IOError> readString();

    Optional<IOError> writeString(CharSequence content);

    boolean isRegularFile();

    Result<Set<PathLike>, IOError> walk();

    String getFileNameAsString();

    String asString();
}
