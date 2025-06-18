package magma.app;

import magma.api.Result;
import magma.api.io.IOError;

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
